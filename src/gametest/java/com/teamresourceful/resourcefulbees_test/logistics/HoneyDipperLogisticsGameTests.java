package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.DipperEntity;
import com.teamresourceful.resourcefulbees.common.components.DipperLogistics;
import com.teamresourceful.resourcefulbees.common.items.logistics.HoneyDipperLogisticsHandler;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.UUID;

public final class HoneyDipperLogisticsGameTests {

    private static final BlockPos CONTROLLER_POS = new BlockPos(1, 2, 1);

    private HoneyDipperLogisticsGameTests() {
    }

    private static ColonyControllerBlockEntity createController(GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, ModBlocks.COLONY_CONTROLLER.get());

        return helper.getBlockEntity(CONTROLLER_POS, ColonyControllerBlockEntity.class);
    }

    private static ItemStack createDipper() {
        return new ItemStack(ModItems.HONEY_DIPPER.get());
    }

    private static DipperLogistics createSelection(GameTestHelper helper, ColonyControllerBlockEntity controller) {
        return new DipperLogistics(helper.getLevel().dimension(), controller.getBlockPos(), controller.getColonyId());
    }

    private static void selectController(ItemStack stack, DipperLogistics selection) {
        stack.remove(ModDataComponents.DIPPER_ENTITY.get());
        stack.set(ModDataComponents.DIPPER_LOGISTICS, selection);
    }

    // 1. Verify controller selection data.
    public static void controllerSelection(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = createDipper();

        DipperLogistics expected = createSelection(helper, controller);
        selectController(stack, expected);

        DipperLogistics actual = stack.get(ModDataComponents.DIPPER_LOGISTICS);

        if (!expected.equals(actual)) {
            helper.fail("Controller selection data does not match");
            return;
        }

        if (!HoneyDipperLogisticsHandler.hasSelection(stack)) {
            helper.fail("Honey Dipper did not report controller selection");
            return;
        }

        helper.succeed();
    }

    // 2. Selecting a controller must clear the bee selection.
    public static void controllerClearsBeeSelection(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = createDipper();

        stack.set(ModDataComponents.DIPPER_ENTITY, DipperEntity.of(UUID.randomUUID()));

        selectController(stack, createSelection(helper, controller));

        if (stack.has(ModDataComponents.DIPPER_ENTITY.get())) {
            helper.fail("Bee selection was not cleared");
            return;
        }

        if (!HoneyDipperLogisticsHandler.hasSelection(stack)) {
            helper.fail("Controller selection was not stored");
            return;
        }

        helper.succeed();
    }

    // 3. Selecting a bee must clear controller selection.
    public static void beeClearsControllerSelection(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = createDipper();

        selectController(stack, createSelection(helper, controller));

        HoneyDipperLogisticsHandler.clearSelection(stack);

        UUID beeId = UUID.randomUUID();

        stack.set(ModDataComponents.DIPPER_ENTITY, DipperEntity.of(beeId));

        if (HoneyDipperLogisticsHandler.hasSelection(stack)) {
            helper.fail("Controller selection was not cleared");
            return;
        }

        DipperEntity selectedBee = stack.get(ModDataComponents.DIPPER_ENTITY);

        if (selectedBee == null || !beeId.equals(selectedBee.uuid())) {
            helper.fail("Bee selection was not stored");
            return;
        }

        helper.succeed();
    }

    // 4. Clearing the controller selection.
    public static void selectionClearing(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = createDipper();

        selectController(stack, createSelection(helper, controller));

        HoneyDipperLogisticsHandler.clearSelection(stack);

        if (HoneyDipperLogisticsHandler.hasSelection(stack)) {
            helper.fail("Controller selection remains after clearing");
            return;
        }

        helper.succeed();
    }

    // 5. Verify item component serialization.
    public static void selectionPersistence(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack original = createDipper();

        DipperLogistics expected = createSelection(helper, controller);
        selectController(original, expected);

        var registryAccess = helper.getLevel().registryAccess();

        // Serialize the ItemStack into NBT.
        var encoded = ItemStack.CODEC.encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), original);

        Tag savedData = encoded.getOrThrow();

        // Deserialize into a new ItemStack.
        var decoded = ItemStack.CODEC.parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), savedData);

        ItemStack restored = decoded.getOrThrow();

        if (restored.isEmpty()) {
            helper.fail("Failed to deserialize Honey Dipper");
            return;
        }

        DipperLogistics actual = restored.get(ModDataComponents.DIPPER_LOGISTICS);

        if (!expected.equals(actual)) {
            helper.fail("Controller selection was not preserved");
            return;
        }

        helper.succeed();
    }

    // 6. A controller with a different UUID must be rejected.
    public static void replacedControllerRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        DipperLogistics selection = new DipperLogistics(helper.getLevel().dimension(), controller.getBlockPos(), UUID.randomUUID());

        if (HoneyDipperLogisticsHandler.isValidSelection(helper.getLevel(), selection)) {
            helper.fail("Controller with mismatched UUID was accepted");
            return;
        }

        helper.succeed();
    }

    // 7. A controller from another dimension must be rejected.
    public static void wrongDimensionRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        ResourceKey<Level> otherDimension = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("resourcefulbees", "test_other_dimension"));

        DipperLogistics selection = new DipperLogistics(otherDimension, controller.getBlockPos(), controller.getColonyId());

        if (HoneyDipperLogisticsHandler.isValidSelection(helper.getLevel(), selection)) {
            helper.fail("Selection from another dimension was accepted");
            return;
        }

        helper.succeed();
    }


    private static final BlockPos SOURCE_POS = new BlockPos(3, 2, 1);
    private static final BlockPos DESTINATION_POS = new BlockPos(5, 2, 1);

    private static CourierEndpoint createInventory(GameTestHelper helper, BlockPos relativePos, Direction side) {
        helper.setBlock(relativePos, Blocks.CHEST);

        return new CourierEndpoint(helper.absolutePos(relativePos), side);
    }

    private static ItemStack selectedDipper(GameTestHelper helper, ColonyControllerBlockEntity controller) {
        ItemStack stack = createDipper();

        HoneyDipperLogisticsHandler.setSelection(stack, createSelection(helper, controller));

        return stack;
    }

    private static void assertResult(GameTestHelper helper, HoneyDipperLogisticsHandler.EndpointResult actual, HoneyDipperLogisticsHandler.EndpointResult expected) {
        if (actual != expected) {
            helper.fail("Expected " + expected + ", got " + actual);
        }
    }

    private static DipperLogistics requireSelection(GameTestHelper helper, ItemStack stack) {
        DipperLogistics selection = stack.get(ModDataComponents.DIPPER_LOGISTICS);

        if (selection == null) {
            helper.fail("Honey Dipper logistics selection is missing");
            throw new IllegalStateException("Missing logistics selection");
        }

        return selection;
    }

    // Test 1: First inventory becomes the source.
    public static void sourceSelection(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        DipperLogistics selection = requireSelection(helper, stack);

        if (!selection.source().equals(Optional.of(source))) {
            helper.fail("Selected source was not stored correctly");
        }

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Selecting a source should not create a route");
        }

        helper.succeed();
    }

    // Test 2: Selecting a destination registers a route.
    public static void routeCreation(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        var first = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        var second = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        assertResult(helper, first, HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, second, HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED);

        if (controller.getRoutes().size() != 1) {
            helper.fail("Expected exactly one registered route");
        }

        CourierRoute route = controller.getRoutes().values().iterator().next();

        if (route.resourceType() != CourierResourceType.ITEM || !route.source().equals(source) || !route.destination().equals(destination) || !route.enabled()) {
            helper.fail("Registered route has incorrect properties");
        }

        helper.succeed();
    }

    // Test 3: Controller stays selected after route creation.
    public static void sourceClearedAfterRoute(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED);

        DipperLogistics selection = requireSelection(helper, stack);

        if (selection.source().isPresent()) {
            helper.fail("Source should be cleared after route creation");
        }

        if (!selection.colonyId().equals(controller.getColonyId()) || !selection.controllerPos().equals(controller.getBlockPos())) {
            helper.fail("Controller selection was not preserved");
        }

        helper.succeed();
    }

    // Test 4: Same endpoint cannot be both source and destination.
    public static void sameEndpointRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.SAME_ENDPOINT);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Same endpoint created a route");
        }

        if (!requireSelection(helper, stack).source().equals(Optional.of(source))) {
            helper.fail("Rejected route should preserve the source");
        }

        helper.succeed();
    }

    // Test 5: Out-of-range inventory is rejected.
    public static void outOfRangeEndpointRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        int radius = controller.getOperatingRadius();

        CourierEndpoint endpoint = new CourierEndpoint(controller.getBlockPos().offset(radius + 1, 0, 0), Direction.UP);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, endpoint);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.OUT_OF_RANGE);

        if (requireSelection(helper, stack).source().isPresent()) {
            helper.fail("Out-of-range endpoint was stored");
        }

        helper.succeed();
    }

    // Test 6: Blocks without an item capability are rejected.
    public static void invalidCapabilityRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        BlockPos relativePos = SOURCE_POS;
        helper.setBlock(relativePos, Blocks.STONE);

        CourierEndpoint endpoint = new CourierEndpoint(helper.absolutePos(relativePos), Direction.UP);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, endpoint);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.NO_CAPABILITY);

        if (requireSelection(helper, stack).source().isPresent()) {
            helper.fail("Invalid inventory was selected");
        }

        helper.succeed();
    }

    // Test 7: Replaced controller invalidates the selection.
    public static void staleControllerRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        // Replace the controller with a non-controller block.
        helper.setBlock(CONTROLLER_POS, Blocks.STONE);

        CourierEndpoint endpoint = createInventory(helper, SOURCE_POS, Direction.UP);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, endpoint);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.CONTROLLER_INVALID);

        if (HoneyDipperLogisticsHandler.hasSelection(stack)) {
            helper.fail("Invalid controller selection was not cleared");
        }

        helper.succeed();
    }

    // Test 8: An unloaded controller is rejected without
// clearing the saved selection.
    public static void unloadedControllerRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = createDipper();

        BlockPos unloadedPos = controller.getBlockPos().offset(1024, 0, 1024);

        DipperLogistics selection = new DipperLogistics(helper.getLevel().dimension(), unloadedPos, controller.getColonyId());

        HoneyDipperLogisticsHandler.setSelection(stack, selection);

        CourierEndpoint endpoint = createInventory(helper, SOURCE_POS, Direction.UP);

        if (helper.getLevel().hasChunkAt(unloadedPos)) {
            helper.fail("Test precondition failed: remote controller chunk is loaded");
            return;
        }

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, endpoint);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.CONTROLLER_UNLOADED);

        if (!HoneyDipperLogisticsHandler.hasSelection(stack)) {
            helper.fail("Unloaded controller should preserve selection");
        }

        helper.succeed();
    }

    // Test 9: Removing the source inventory prevents route creation.
    public static void invalidSourceRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        helper.setBlock(SOURCE_POS, Blocks.STONE);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        assertResult(helper, result, HoneyDipperLogisticsHandler.EndpointResult.SOURCE_INVALID);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Route was created with an invalid source");
        }

        helper.succeed();
    }

    // Test 10: Two valid routes can be created consecutively.
    public static void consecutiveRouteCreation(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        var firstSource = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        var firstRoute = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        // Reverse direction to create a distinct route.
        var secondSource = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        var secondRoute = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        assertResult(helper, firstSource, HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, firstRoute, HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED);

        assertResult(helper, secondSource, HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, secondRoute, HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED);

        if (controller.getRoutes().size() != 2) {
            helper.fail("Expected two distinct routes");
        }

        if (requireSelection(helper, stack).source().isPresent()) {
            helper.fail("Source should be cleared after second route");
        }

        helper.succeed();
    }


    public static void sourcePersistence(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack original = createDipper();

        CourierEndpoint source = new CourierEndpoint(helper.absolutePos(new BlockPos(3, 2, 1)), Direction.NORTH);

        DipperLogistics selection = createSelection(helper, controller).withSource(source);

        HoneyDipperLogisticsHandler.setSelection(original, selection);

        var registryAccess = helper.getLevel().registryAccess();

        Tag encoded = ItemStack.CODEC.encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), original).getOrThrow();

        ItemStack restored = ItemStack.CODEC.parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), encoded).getOrThrow();

        DipperLogistics restoredSelection = restored.get(ModDataComponents.DIPPER_LOGISTICS);

        if (restoredSelection == null) {
            helper.fail("Logistics selection was lost during serialization");
            return;
        }

        if (!restoredSelection.dimension().equals(selection.dimension()) || !restoredSelection.controllerPos().equals(selection.controllerPos()) || !restoredSelection.colonyId().equals(selection.colonyId())) {
            helper.fail("Controller selection changed during serialization");
            return;
        }

        if (!restoredSelection.source().equals(Optional.of(source))) {
            helper.fail("Source position or face changed during serialization");
            return;
        }

        helper.succeed();
    }

    //todo add TestBlocks.SIDED_INVENTORY to perform test
//    public static void sidedCapabilityValidation(GameTestHelper helper) {
//        ColonyControllerBlockEntity controller = createController(helper);
//        ItemStack stack = createDipper();
//
//        HoneyDipperLogisticsHandler.setSelection(
//                stack,
//                createSelection(helper, controller)
//        );
//
//        BlockPos relativePos = new BlockPos(3, 2, 1);
//
//        // Replace with your registered test-only sided inventory.
//        helper.setBlock(
//                relativePos,
//                TestBlocks.SIDED_INVENTORY.get()
//        );
//
//        BlockPos absolutePos = helper.absolutePos(relativePos);
//
//        CourierEndpoint blockedFace = new CourierEndpoint(
//                absolutePos,
//                Direction.DOWN
//        );
//
//        CourierEndpoint allowedFace = new CourierEndpoint(
//                absolutePos,
//                Direction.UP
//        );
//
//        var rejected = HoneyDipperLogisticsHandler.processEndpoint(
//                helper.getLevel(),
//                stack,
//                blockedFace
//        );
//
//        if (rejected != HoneyDipperLogisticsHandler.EndpointResult.NO_ITEM_CAPABILITY) {
//            helper.fail("Expected inaccessible face to be rejected, got " + rejected);
//            return;
//        }
//
//        DipperLogistics selection = stack.get(
//                ModDataComponents.DIPPER_LOGISTICS
//        );
//
//        if (selection == null || selection.source().isPresent()) {
//            helper.fail("Rejected face modified the source selection");
//            return;
//        }
//
//        var accepted = HoneyDipperLogisticsHandler.processEndpoint(
//                helper.getLevel(),
//                stack,
//                allowedFace
//        );
//
//        if (accepted != HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED) {
//            helper.fail("Expected accessible face to be accepted, got " + accepted);
//            return;
//        }
//
//        selection = stack.get(ModDataComponents.DIPPER_LOGISTICS);
//
//        if (selection == null
//                || !selection.source().equals(Optional.of(allowedFace))) {
//            helper.fail("Accessible face was not stored correctly");
//            return;
//        }
//
//        helper.succeed();
//    }


    public static void routeInferenceItemOnly(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);
        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        var selected = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);
        var created = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        if (selected != HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED || created != HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED) {
            helper.fail("ITEM capability inference failed: " + selected + ", " + created);
            return;
        }

        if (controller.getRoutes().size() != 1) {
            helper.fail("Expected exactly one inferred route");
            return;
        }

        CourierRoute route = controller.getRoutes().values().iterator().next();

        if (route.resourceType() != CourierResourceType.ITEM) {
            helper.fail("Expected ITEM route, got " + route.resourceType());
            return;
        }

        helper.succeed();
    }

    public static void routeInferenceNoCapability(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        helper.setBlock(SOURCE_POS, Blocks.STONE);

        CourierEndpoint endpoint = new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, endpoint);

        if (result != HoneyDipperLogisticsHandler.EndpointResult.NO_CAPABILITY) {
            helper.fail("Expected NO_CAPABILITY, got " + result);
            return;
        }

        if (requireSelection(helper, stack).source().isPresent()) {
            helper.fail("Unsupported endpoint was saved as source");
            return;
        }

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Unsupported endpoint created a route");
            return;
        }

        helper.succeed();
    }

    public static void routeInferenceInvalidSourcePreserved(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);
        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        var selected = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);

        if (selected != HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED) {
            helper.fail("Failed to select source");
            return;
        }

        helper.setBlock(SOURCE_POS, Blocks.STONE);

        var result = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        if (result != HoneyDipperLogisticsHandler.EndpointResult.SOURCE_INVALID) {
            helper.fail("Expected SOURCE_INVALID, got " + result);
            return;
        }

        if (!requireSelection(helper, stack).source().equals(Optional.of(source))) {
            helper.fail("Rejected route lost the selected source");
            return;
        }

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Invalid source created a route");
            return;
        }

        helper.succeed();
    }

    public static void routeInferenceControllerRetained(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);
        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        var selected = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source);
        var created = HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination);

        if (selected != HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED || created != HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED) {
            helper.fail("Route setup failed: " + selected + ", " + created);
            return;
        }

        DipperLogistics selection = requireSelection(helper, stack);

        if (selection.source().isPresent() || !selection.controllerPos().equals(controller.getBlockPos()) || !selection.colonyId().equals(controller.getColonyId())) {
            helper.fail("Controller selection was not retained correctly");
            return;
        }

        if (controller.getRoutes().size() != 1) {
            helper.fail("Expected one created route");
            return;
        }

        helper.succeed();
    }

    public static void ambiguousFluidRoute(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        helper.setBlock(SOURCE_POS, ModBlocks.HONEY_GENERATOR.get());
        helper.setBlock(DESTINATION_POS, ModBlocks.HONEY_GENERATOR.get());

        CourierEndpoint source = new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP);
        CourierEndpoint destination = new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination), HoneyDipperLogisticsHandler.EndpointResult.AMBIGUOUS_CAPABILITY);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Ambiguous selection created a route prematurely");
            return;
        }

        assertResult(helper, HoneyDipperLogisticsHandler.confirmRouteType(helper.getLevel(), stack, destination, CourierResourceType.FLUID), HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED);

        if (controller.getRoutes().size() != 1) {
            helper.fail("Expected exactly one FLUID route");
            return;
        }

        CourierRoute route = controller.getRoutes().values().iterator().next();

        if (route.resourceType() != CourierResourceType.FLUID || !route.source().equals(source) || !route.destination().equals(destination)) {
            helper.fail("Incorrect FLUID route created");
            return;
        }

        if (requireSelection(helper, stack).source().isPresent()) {
            helper.fail("Successful confirmation did not clear source");
            return;
        }

        helper.succeed();
    }

    public static void ambiguousEnergyRoute(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        helper.setBlock(SOURCE_POS, ModBlocks.HONEY_GENERATOR.get());
        helper.setBlock(DESTINATION_POS, ModBlocks.HONEY_GENERATOR.get());

        CourierEndpoint source = new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP);
        CourierEndpoint destination = new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination), HoneyDipperLogisticsHandler.EndpointResult.AMBIGUOUS_CAPABILITY);

        assertResult(helper, HoneyDipperLogisticsHandler.confirmRouteType(helper.getLevel(), stack, destination, CourierResourceType.ENERGY), HoneyDipperLogisticsHandler.EndpointResult.ROUTE_CREATED);

        if (controller.getRoutes().size() != 1) {
            helper.fail("Expected exactly one ENERGY route");
            return;
        }

        CourierRoute route = controller.getRoutes().values().iterator().next();

        if (route.resourceType() != CourierResourceType.ENERGY) {
            helper.fail("Expected ENERGY route");
            return;
        }

        helper.succeed();
    }

    public static void unsupportedResourceTypeRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        helper.setBlock(SOURCE_POS, ModBlocks.HONEY_POT.get());
        helper.setBlock(DESTINATION_POS, ModBlocks.HONEY_POT.get());

        CourierEndpoint source = new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP);
        CourierEndpoint destination = new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, HoneyDipperLogisticsHandler.confirmRouteType(helper.getLevel(), stack, destination, CourierResourceType.ENERGY), HoneyDipperLogisticsHandler.EndpointResult.NO_MATCHING_CAPABILITY);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Unsupported resource type created a route");
            return;
        }

        if (!requireSelection(helper, stack).source().equals(Optional.of(source))) {
            helper.fail("Rejected confirmation cleared source");
            return;
        }

        helper.succeed();
    }

    public static void mismatchedCapabilitiesRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        helper.setBlock(DESTINATION_POS, ModBlocks.HONEY_POT.get());

        CourierEndpoint destination = new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, destination), HoneyDipperLogisticsHandler.EndpointResult.NO_MATCHING_CAPABILITY);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Mismatched endpoints created a route");
            return;
        }

        if (!requireSelection(helper, stack).source().equals(Optional.of(source))) {
            helper.fail("Mismatch cleared selected source");
            return;
        }

        helper.succeed();
    }

    public static void staleDestinationRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        helper.setBlock(SOURCE_POS, ModBlocks.HONEY_GENERATOR.get());
        helper.setBlock(DESTINATION_POS, ModBlocks.HONEY_GENERATOR.get());

        CourierEndpoint source = new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP);
        CourierEndpoint destination = new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        helper.setBlock(DESTINATION_POS, Blocks.STONE);

        assertResult(helper, HoneyDipperLogisticsHandler.confirmRouteType(helper.getLevel(), stack, destination, CourierResourceType.FLUID), HoneyDipperLogisticsHandler.EndpointResult.NO_CAPABILITY);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Stale destination created a route");
            return;
        }

        if (!requireSelection(helper, stack).source().equals(Optional.of(source))) {
            helper.fail("Stale destination cleared source");
            return;
        }

        helper.succeed();
    }

    public static void staleSourceRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);
        ItemStack stack = selectedDipper(helper, controller);

        helper.setBlock(SOURCE_POS, ModBlocks.HONEY_GENERATOR.get());
        helper.setBlock(DESTINATION_POS, ModBlocks.HONEY_GENERATOR.get());

        CourierEndpoint source = new CourierEndpoint(helper.absolutePos(SOURCE_POS), Direction.UP);
        CourierEndpoint destination = new CourierEndpoint(helper.absolutePos(DESTINATION_POS), Direction.UP);

        assertResult(helper, HoneyDipperLogisticsHandler.processEndpoint(helper.getLevel(), stack, source), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_SELECTED);

        helper.setBlock(SOURCE_POS, Blocks.STONE);

        assertResult(helper, HoneyDipperLogisticsHandler.confirmRouteType(helper.getLevel(), stack, destination, CourierResourceType.FLUID), HoneyDipperLogisticsHandler.EndpointResult.SOURCE_INVALID);

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("Stale source created a route");
            return;
        }

        if (!requireSelection(helper, stack).source().equals(Optional.of(source))) {
            helper.fail("Stale source cleared selection");
            return;
        }

        helper.succeed();
    }

}
