
package com.teamresourceful.resourcefulbees.common.items.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.DipperLogistics;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.HoneyDipperTranslations;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.OpenCourierRouteSelectorPacket;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.EnumSet;
import java.util.UUID;

public final class HoneyDipperLogisticsHandler {

    private HoneyDipperLogisticsHandler() {
    }

    public enum EndpointResult {
        NO_SELECTION,
        WRONG_DIMENSION,
        CONTROLLER_UNLOADED,
        CONTROLLER_INVALID,
        OUT_OF_RANGE,
        SOURCE_SELECTED,
        SAME_ENDPOINT,
        SOURCE_INVALID,
        ROUTE_REJECTED,
        ROUTE_CREATED,
        NO_CAPABILITY,
        NO_MATCHING_CAPABILITY,
        AMBIGUOUS_CAPABILITY,
    }

    public static boolean hasSelection(ItemStack stack) {
        return stack.has(ModDataComponents.DIPPER_LOGISTICS.get());
    }

    public static void clearSelection(ItemStack stack) {
        stack.remove(ModDataComponents.DIPPER_LOGISTICS.get());
    }

    public static void setSelection(ItemStack stack, DipperLogistics selection) {
        stack.remove(ModDataComponents.DIPPER_ENTITY.get());
        stack.set(ModDataComponents.DIPPER_LOGISTICS, selection);
    }

    public static InteractionResult selectController(UseOnContext context, ColonyControllerBlockEntity controller) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();

        DipperLogistics selection = new DipperLogistics(
                level.dimension(),
                controller.getBlockPos(),
                controller.getColonyId()
        );

        setSelection(stack, selection);

        player.sendSystemMessage(Component.translatable(
                HoneyDipperTranslations.CONTROLLER_SET,
                selection.controllerPos().toShortString()
        ));

        return InteractionResult.SUCCESS;
    }

    public static boolean isValidSelection(ServerLevel level, DipperLogistics selection) {
        return resolveController(level, selection) != null;
    }

    public static InteractionResult handleEndpoint(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();

        CourierEndpoint endpoint = new CourierEndpoint(context.getClickedPos(), context.getClickedFace());
        EndpointResult result = processEndpoint(level, stack, endpoint);

        if (result == EndpointResult.AMBIGUOUS_CAPABILITY) {
            DipperLogistics selection = stack.get(ModDataComponents.DIPPER_LOGISTICS);

            if (selection != null && selection.source().isPresent()) {
                EnumSet<CourierResourceType> matches =
                        matchingCapabilities(level, selection.source().orElseThrow(), endpoint);

                NetworkHandler.NETWORK.sendToPlayer(
                        OpenCourierRouteSelectorPacket.of(endpoint, matches, context.getHand()),
                        player
                );

                return InteractionResult.SUCCESS;
            }
        }

        if (result == EndpointResult.NO_SELECTION) {
            return InteractionResult.PASS;
        }

        sendEndpointMessage(player, result, endpoint);

        return InteractionResult.SUCCESS;
    }

    @VisibleForDebug
    public static EndpointResult processEndpoint(ServerLevel level, ItemStack stack, CourierEndpoint endpoint) {
        DipperLogistics selection = stack.get(
                ModDataComponents.DIPPER_LOGISTICS
        );

        if (selection == null) {
            return EndpointResult.NO_SELECTION;
        }

        EndpointResult controllerStatus = validateController(level, selection);

        if (controllerStatus != null) {
            if (controllerStatus == EndpointResult.CONTROLLER_INVALID) {
                clearSelection(stack);
            }

            return controllerStatus;
        }

        ColonyControllerBlockEntity controller = resolveController(level, selection);

        // validateController() guarantees this is present.
        if (controller == null) {
            return EndpointResult.CONTROLLER_INVALID;
        }

        if (!isWithinRadius(controller, endpoint.pos())) {
            return EndpointResult.OUT_OF_RANGE;
        }

        if (detectCapabilities(level, endpoint).isEmpty()) {
            return EndpointResult.NO_CAPABILITY;
        }

        if (selection.source().isEmpty()) {
            return selectSource(stack, selection, endpoint);
        }

        return createRoute(level, stack, controller, selection, endpoint);
    }

    /**
     * Returns null when the controller is valid.
     */
    private static EndpointResult validateController(ServerLevel level, DipperLogistics selection) {
        if (!level.dimension().equals(selection.dimension())) {
            return EndpointResult.WRONG_DIMENSION;
        }

        if (!level.hasChunkAt(selection.controllerPos())) {
            return EndpointResult.CONTROLLER_UNLOADED;
        }

        if (resolveController(level, selection) == null) {
            return EndpointResult.CONTROLLER_INVALID;
        }

        return null;
    }

    private static ColonyControllerBlockEntity resolveController(ServerLevel level, DipperLogistics selection) {
        if (!level.dimension().equals(selection.dimension())) {
            return null;
        }

        if (!level.hasChunkAt(selection.controllerPos())) {
            return null;
        }

        BlockEntity blockEntity = level.getBlockEntity(selection.controllerPos());

        if (blockEntity instanceof ColonyControllerBlockEntity controller
                && controller.getColonyId().equals(selection.colonyId())) {
            return controller;
        }

        return null;
    }

    @VisibleForDebug
    public static EndpointResult selectSource(ItemStack stack, DipperLogistics selection, CourierEndpoint endpoint) {
        stack.set(
                ModDataComponents.DIPPER_LOGISTICS,
                selection.withSource(endpoint)
        );

        return EndpointResult.SOURCE_SELECTED;
    }

    private static EndpointResult createRoute(ServerLevel level, ItemStack stack, ColonyControllerBlockEntity controller, DipperLogistics selection, CourierEndpoint destination) {
        CourierEndpoint source = selection.source().orElseThrow();

        if (source.equals(destination)) {
            return EndpointResult.SAME_ENDPOINT;
        }

        if (!isValidEndpoint(level, controller, source)) {
            return EndpointResult.SOURCE_INVALID;
        }


        EnumSet<CourierResourceType> matches =
                matchingCapabilities(level, source, destination);

        if (matches.isEmpty()) {
            return EndpointResult.NO_MATCHING_CAPABILITY;
        }

        if (matches.size() > 1) {
            return EndpointResult.AMBIGUOUS_CAPABILITY;
        }

        CourierResourceType resourceType = matches.iterator().next();

        return registerRoute(stack, controller, selection, source, destination, resourceType);
    }


    private static boolean isValidEndpoint(
            ServerLevel level,
            ColonyControllerBlockEntity controller,
            CourierEndpoint endpoint
    ) {
        return controller.isWithinRange(endpoint.pos())
                && !detectCapabilities(level, endpoint).isEmpty();
    }


    private static boolean isWithinRadius(ColonyControllerBlockEntity controller, BlockPos endpoint) {
        BlockPos origin = controller.getBlockPos();
        long radius = controller.getOperatingRadius();

        return origin.distSqr(endpoint) <= radius * radius;
    }


    private static EnumSet<CourierResourceType> matchingCapabilities(
            ServerLevel level,
            CourierEndpoint source,
            CourierEndpoint destination
    ) {
        EnumSet<CourierResourceType> matches =
                detectCapabilities(level, source);

        matches.retainAll(detectCapabilities(level, destination));

        return matches;
    }



    private static EnumSet<CourierResourceType> detectCapabilities(
            ServerLevel level,
            CourierEndpoint endpoint
    ) {
        EnumSet<CourierResourceType> types =
                EnumSet.noneOf(CourierResourceType.class);

        if (!level.hasChunkAt(endpoint.pos())) {
            return types;
        }

        var pos = endpoint.pos();
        var side = endpoint.side();

        if (level.getCapability(Capabilities.Item.BLOCK, pos, side) != null) {
            types.add(CourierResourceType.ITEM);
        }

        if (level.getCapability(Capabilities.Fluid.BLOCK, pos, side) != null) {
            types.add(CourierResourceType.FLUID);
        }

        if (level.getCapability(Capabilities.Energy.BLOCK, pos, side) != null) {
            types.add(CourierResourceType.ENERGY);
        }

        return types;
    }

    private static EndpointResult registerRoute(
            ItemStack stack,
            ColonyControllerBlockEntity controller,
            DipperLogistics selection,
            CourierEndpoint source,
            CourierEndpoint destination,
            CourierResourceType resourceType
    ) {
        CourierRoute route = new CourierRoute(
                UUID.randomUUID(),
                resourceType,
                source,
                destination,
                true,
                0
        );

        if (!controller.addRoute(route)) {
            return EndpointResult.ROUTE_REJECTED;
        }

        stack.set(
                ModDataComponents.DIPPER_LOGISTICS,
                selection.clearSource()
        );

        return EndpointResult.ROUTE_CREATED;
    }


    public static EndpointResult confirmRouteType(
            ServerLevel level,
            ItemStack stack,
            CourierEndpoint destination,
            CourierResourceType selectedType
    ) {
        DipperLogistics selection =
                stack.get(ModDataComponents.DIPPER_LOGISTICS);

        if (selection == null) {
            return EndpointResult.NO_SELECTION;
        }

        // Revalidate the selected controller.
        EndpointResult controllerStatus =
                validateController(level, selection);

        if (controllerStatus != null) {
            if (controllerStatus == EndpointResult.CONTROLLER_INVALID) {
                clearSelection(stack);
            }

            return controllerStatus;
        }

        ColonyControllerBlockEntity controller =
                resolveController(level, selection);

        if (controller == null) {
            return EndpointResult.CONTROLLER_INVALID;
        }

        // A source must still be selected.
        if (selection.source().isEmpty()) {
            return EndpointResult.NO_SELECTION;
        }

        CourierEndpoint source = selection.source().orElseThrow();

        // Reject identical endpoints.
        if (source.equals(destination)) {
            return EndpointResult.SAME_ENDPOINT;
        }

        // Revalidate the source, including its clicked face.
        if (!isValidEndpoint(level, controller, source)) {
            return EndpointResult.SOURCE_INVALID;
        }

        // Validate the destination against the operating radius.
        if (!isWithinRadius(controller, destination.pos())) {
            return EndpointResult.OUT_OF_RANGE;
        }

        // Ensure the destination still exposes a capability.
        if (detectCapabilities(level, destination).isEmpty()) {
            return EndpointResult.NO_CAPABILITY;
        }

        // Recompute the intersection; never trust the client.
        EnumSet<CourierResourceType> matches =
                matchingCapabilities(level, source, destination);

        if (matches.isEmpty() || !matches.contains(selectedType)) {
            return EndpointResult.NO_MATCHING_CAPABILITY;
        }

        return registerRoute(stack, controller, selection, source, destination, selectedType);
    }


    public static void sendConfirmationMessage(
            Player player,
            EndpointResult result,
            CourierEndpoint endpoint
    ) {
        sendEndpointMessage(player, result, endpoint);
    }

    private static void sendEndpointMessage(
            Player player,
            EndpointResult result,
            CourierEndpoint endpoint
    ) {
        Component message = switch (result) {
            case NO_SELECTION -> null;
            case WRONG_DIMENSION -> Component.translatable(HoneyDipperTranslations.WRONG_DIMENSION);
            case CONTROLLER_UNLOADED -> Component.translatable(HoneyDipperTranslations.CONTROLLER_UNLOADED);
            case CONTROLLER_INVALID -> Component.translatable(HoneyDipperTranslations.CONTROLLER_INVALID);
            case OUT_OF_RANGE -> Component.translatable(HoneyDipperTranslations.OUT_OF_RANGE);
            case NO_CAPABILITY -> Component.translatable(HoneyDipperTranslations.NO_CAPABILITY);
            case SOURCE_SELECTED -> Component.translatable(HoneyDipperTranslations.SOURCE_SELECTED, endpoint.pos().toShortString());
            case SAME_ENDPOINT -> Component.translatable(HoneyDipperTranslations.SAME_ENDPOINT);
            case SOURCE_INVALID -> Component.translatable(HoneyDipperTranslations.SOURCE_INVALID);
            case ROUTE_REJECTED -> Component.translatable(HoneyDipperTranslations.ROUTE_REJECTED);
            case ROUTE_CREATED -> Component.translatable(HoneyDipperTranslations.ROUTE_CREATED, endpoint.pos().toShortString());
            case NO_MATCHING_CAPABILITY -> Component.translatable(HoneyDipperTranslations.NO_MATCHING_CAPABILITY);
            case AMBIGUOUS_CAPABILITY -> Component.translatable(HoneyDipperTranslations.AMBIGUOUS_CAPABILITY);
        };

        if (message != null) {
            player.sendSystemMessage(message);
        }
    }

}
