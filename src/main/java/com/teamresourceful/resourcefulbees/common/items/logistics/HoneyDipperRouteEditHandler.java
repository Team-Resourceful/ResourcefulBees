package com.teamresourceful.resourcefulbees.common.items.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteEndpointEdit;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.capabilities.Capabilities;

public final class HoneyDipperRouteEditHandler {

    private HoneyDipperRouteEditHandler() {
    }

    public static InteractionResult handleEndpoint(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();

        CourierRouteEndpointEdit edit = stack.get(ModDataComponents.DIPPER_ROUTE_EDIT);

        if (edit == null) {
            return InteractionResult.PASS;
        }

        if (!level.dimension().equals(edit.dimension())) {
            player.sendSystemMessage(Component.literal("The selected route belongs to another dimension."));
            return InteractionResult.FAIL;
        }

        if (!level.hasChunkAt(edit.controllerPos())) {
            player.sendSystemMessage(Component.literal("The colony controller is not loaded."));
            return InteractionResult.FAIL;
        }

        if (!(level.getBlockEntity(edit.controllerPos()) instanceof ColonyControllerBlockEntity controller) || !controller.getColonyId().equals(edit.colonyId())) {
            player.sendSystemMessage(Component.literal("The selected colony controller is no longer valid."));
            return InteractionResult.FAIL;
        }

        CourierRoute route = controller.getRoutes().get(edit.routeId());

        if (route == null) {
            player.sendSystemMessage(Component.literal("The selected route no longer exists."));
            return InteractionResult.FAIL;
        }

        CourierEndpoint endpoint = new CourierEndpoint(context.getClickedPos(), context.getClickedFace());

        if (!controller.isWithinRange(endpoint.pos())) {
            player.sendSystemMessage(Component.literal("The endpoint is outside the colony operating radius."));
            return InteractionResult.FAIL;
        }

        boolean hasCapability = switch (route.resourceType()) {
            case ITEM -> level.getCapability(Capabilities.Item.BLOCK, endpoint.pos(), endpoint.side()) != null;
            case FLUID -> level.getCapability(Capabilities.Fluid.BLOCK, endpoint.pos(), endpoint.side()) != null;
            case ENERGY -> level.getCapability(Capabilities.Energy.BLOCK, endpoint.pos(), endpoint.side()) != null;
        };

        if (!hasCapability) {
            player.sendSystemMessage(Component.literal("This block does not expose the route's resource capability."));
            return InteractionResult.FAIL;
        }

        if (!controller.queueRouteEndpointUpdate(edit.routeId(), endpoint, edit.endpointType())) {
            player.sendSystemMessage(Component.literal("Unable to update the selected route endpoint."));
            return InteractionResult.FAIL;
        }

        stack.remove(ModDataComponents.DIPPER_ROUTE_EDIT.get());

        player.sendSystemMessage(Component.literal("Route endpoint update queued successfully."));

        return InteractionResult.SUCCESS;
    }
}
