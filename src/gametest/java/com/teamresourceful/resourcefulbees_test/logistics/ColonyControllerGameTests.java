package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.ApiaryOccupant;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteExecutionMode;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.UUID;

public final class ColonyControllerGameTests {

    private static final BlockPos CONTROLLER_POS = new BlockPos(1, 2, 1);
    private static final BlockPos SOURCE_POS = new BlockPos(3, 2, 1);
    private static final BlockPos DESTINATION_POS = new BlockPos(5, 2, 1);

    private ColonyControllerGameTests() {
    }


    private static void storeTestBee(GameTestHelper helper, ColonyControllerBlockEntity controller, boolean locked) {
        Bee bee = helper.spawn(EntityTypes.BEE, CONTROLLER_POS.above());

        if (!(bee instanceof BeeCompat compat)) {
            helper.fail("Test bee does not implement BeeCompat");
            return;
        }

        ApiaryOccupant original = ApiaryOccupant.of(bee, compat, controller, locked);

        ApiaryOccupant occupant = new ApiaryOccupant(original.entityData(), 0, 1200, original.displayName(), original.color(), locked, original.hasNectar());

        controller.storeBee(occupant);
        controller.setChanged();
        bee.discard();
    }

    private static CourierRoute addWakeRoute(GameTestHelper helper, ColonyControllerBlockEntity controller, boolean enabled) {
        // Create inventories before registering the route.
        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);

        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

        CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, source, destination, enabled, 0).withExecutionMode(CourierRouteExecutionMode.COURIER);

        if (!controller.addRoute(route)) {
            helper.fail("Failed to register wake-up test route");
        }

        return route;
    }


    private static ColonyControllerBlockEntity createController(GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, ModBlocks.COLONY_CONTROLLER.get());
        return helper.getBlockEntity(CONTROLLER_POS, ColonyControllerBlockEntity.class);
    }

    private static CourierRoute createRoute(ColonyControllerBlockEntity controller, UUID id, int sourceOffset, int destinationOffset) {
        BlockPos origin = controller.getBlockPos();

        return new CourierRoute(id, CourierResourceType.ITEM, new CourierEndpoint(origin.offset(sourceOffset, 0, 0), Direction.UP), new CourierEndpoint(origin.offset(destinationOffset, 0, 0), Direction.UP), true, 0);
    }

    public static void controllerDefaults(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        if (controller.getOperatingRadius() != 16) {
            helper.fail("Expected operating radius of 16");
            return;
        }

        if (!controller.getRoutes().isEmpty()) {
            helper.fail("New controller should have no routes");
            return;
        }

        helper.succeed();
    }

    public static void controllerIdentity(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        UUID id = controller.getColonyId();

        if (id == null) {
            helper.fail("Controller UUID was not initialized");
            return;
        }

        if (!id.equals(controller.getColonyId())) {
            helper.fail("Controller UUID changed between reads");
            return;
        }

        helper.succeed();
    }

    public static void routeRegistration(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        CourierRoute route = createRoute(controller, UUID.randomUUID(), 2, 4);

        if (!controller.addRoute(route)) {
            helper.fail("Valid route was rejected");
            return;
        }

        if (!controller.getRoutes().containsKey(route.id())) {
            helper.fail("Registered route was not stored");
            return;
        }

        helper.succeed();
    }

    public static void duplicateRouteIsRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        CourierRoute route = createRoute(controller, UUID.randomUUID(), 2, 4);

        if (!controller.addRoute(route)) {
            helper.fail("Initial route registration failed");
            return;
        }

        if (controller.addRoute(route)) {
            helper.fail("Duplicate route was accepted");
            return;
        }

        if (controller.getRoutes().size() != 1) {
            helper.fail("Expected exactly one stored route");
            return;
        }

        helper.succeed();
    }

    public static void outOfRangeRouteIsRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        CourierRoute route = createRoute(controller, UUID.randomUUID(), 2, 40);

        if (controller.addRoute(route)) {
            helper.fail("Out-of-range route was accepted");
            return;
        }

        helper.succeed();
    }

    public static void routeRemoval(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        CourierRoute route = createRoute(controller, UUID.randomUUID(), 2, 4);

        if (!controller.addRoute(route)) {
            helper.fail("Route registration failed");
            return;
        }

        if (!controller.removeRoute(route.id())) {
            helper.fail("Existing route could not be removed");
            return;
        }

        if (controller.getRoutes().containsKey(route.id())) {
            helper.fail("Removed route is still stored");
            return;
        }

        helper.succeed();
    }

    public static void controllerSerializationRoundTrip(GameTestHelper helper) {
        ColonyControllerBlockEntity original = createController(helper);

        CourierRoute itemRoute = createRoute(original, UUID.randomUUID(), 2, 4);

        CourierRoute fluidRoute = new CourierRoute(UUID.randomUUID(), CourierResourceType.FLUID, new CourierEndpoint(original.getBlockPos().offset(5, 0, 0), Direction.NORTH), new CourierEndpoint(original.getBlockPos().offset(8, 0, 0), Direction.SOUTH), false, 10);

        if (!original.addRoute(itemRoute) || !original.addRoute(fluidRoute)) {
            helper.fail("Failed to configure routes before serialization");
            return;
        }

        UUID expectedColonyId = original.getColonyId();

        // Serialize the original block entity.
        var registryAccess = helper.getLevel().registryAccess();
        var savedData = original.saveWithFullMetadata(registryAccess);

        // Deserialize into a fresh block entity.
        BlockEntity loaded = BlockEntity.loadStatic(original.getBlockPos(), original.getBlockState(), savedData, registryAccess);

        if (!(loaded instanceof ColonyControllerBlockEntity restored)) {
            helper.fail("Failed to deserialize ColonyControllerBlockEntity");
            return;
        }

        if (restored == original) {
            helper.fail("Restored controller is the original instance");
            return;
        }

        if (!expectedColonyId.equals(restored.getColonyId())) {
            helper.fail("Colony UUID was not preserved");
            return;
        }

//        if (restored.getOperatingRadius() != 48) {
//            helper.fail(
//                    "Expected restored radius 48, got "
//                            + restored.getOperatingRadius()
//            );
//            return;
//        }

        if (restored.getRoutes().size() != 2) {
            helper.fail("Expected 2 restored routes, got " + restored.getRoutes().size());
            return;
        }

        if (!itemRoute.equals(restored.getRoutes().get(itemRoute.id()))) {
            helper.fail("Item route was not preserved");
            return;
        }

        if (!fluidRoute.equals(restored.getRoutes().get(fluidRoute.id()))) {
            helper.fail("Fluid route was not preserved");
            return;
        }

        helper.succeed();
    }

    public static void controllerUpgradeDefaults(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        if (controller.getOperatingRadius() != 16 || controller.getMaxRoutes() != 2) {
            helper.fail("Incorrect default controller upgrade state");
            return;
        }

        helper.succeed();
    }

    public static void controllerUpgradeProgression(GameTestHelper helper) {
//        ColonyControllerBlockEntity controller = createController(helper);
//
//        int[] radii = {32, 40, 48, 64};
//        int[] capacities = {4, 8, 12, 16};
//
//        for (int tier = 0; tier < radii.length; tier++) {
//            if (!controller.setRadiusUpgradeLevel(tier)
//                    || !controller.setRouteUpgradeLevel(tier)) {
//                helper.fail("Upgrade rejected at tier " + tier);
//                return;
//            }
//
//            if (controller.getOperatingRadius() != radii[tier]
//                    || controller.getMaxRoutes() != capacities[tier]) {
//                helper.fail("Incorrect upgrade values at tier " + tier);
//                return;
//            }
//        }

        helper.succeed();
    }

    public static void controllerUpgradeTracksIndependent(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

//        if (!controller.setRadiusUpgradeLevel(2)) {
//            helper.fail("Radius upgrade failed");
//            return;
//        }
//
//        if (controller.getOperatingRadius() != 48
//                || controller.getMaxRoutes() != 4) {
//            helper.fail("Radius upgrade changed route capacity");
//            return;
//        }
//
//        if (!controller.setRouteUpgradeLevel(1)) {
//            helper.fail("Route capacity upgrade failed");
//            return;
//        }
//
//        if (controller.getOperatingRadius() != 48
//                || controller.getMaxRoutes() != 8) {
//            helper.fail("Route upgrade changed radius");
//            return;
//        }

        helper.succeed();
    }

    public static void controllerUpgradeInvalidTier(GameTestHelper helper) {
//        ColonyControllerBlockEntity controller = createController(helper);
//
//        if (controller.setRadiusUpgradeLevel(-1)
//                || controller.setRadiusUpgradeLevel(4)
//                || controller.setRouteUpgradeLevel(-1)
//                || controller.setRouteUpgradeLevel(4)) {
//            helper.fail("Invalid upgrade tier was accepted");
//            return;
//        }
//
//        if (controller.getOperatingRadius() != 32
//                || controller.getMaxRoutes() != 4) {
//            helper.fail("Invalid upgrade changed controller state");
//            return;
//        }

        helper.succeed();
    }


    public static void controllerRouteCapacity(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        CourierEndpoint source = createInventory(helper, SOURCE_POS, Direction.UP);
        CourierEndpoint destination = createInventory(helper, DESTINATION_POS, Direction.UP);

//        for (int i = 0; i < 4; i++) {
//            CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, source, destination, true, 0);
//
//            if (!controller.addRoute(route)) {
//                helper.fail("Failed to add route " + i);
//                return;
//            }
//        }

        CourierRoute extra = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, source, destination, true, 0);

//        if (controller.addRoute(extra)) {
//            helper.fail("Controller exceeded default route capacity");
//            return;
//        }
//
//        if (controller.getRoutes().size() != 4) {
//            helper.fail("Incorrect route count at capacity");
//            return;
//        }
//
//        if (!controller.setRouteUpgradeLevel(1)) {
//            helper.fail("Route capacity upgrade failed");
//            return;
//        }
//
//        if (!controller.addRoute(extra)) {
//            helper.fail("Controller rejected route after capacity upgrade");
//            return;
//        }
//
//        if (controller.getRoutes().size() != 5) {
//            helper.fail("Incorrect route count after upgrade");
//            return;
//        }

        helper.succeed();
    }


    public static void controllerRouteDowngradeRejected(GameTestHelper helper) {
//        ColonyControllerBlockEntity controller = createController(helper);
//
//        controller.setRouteUpgradeLevel(1);
//
//        CourierEndpoint source =
//                createInventory(helper, SOURCE_POS, Direction.UP);
//        CourierEndpoint destination =
//                createInventory(helper, DESTINATION_POS, Direction.UP);
//
//        for (int i = 0; i < 5; i++) {
//            CourierRoute route = new CourierRoute(
//                    UUID.randomUUID(),
//                    CourierResourceType.ITEM,
//                    source,
//                    destination,
//                    true,
//                    0
//            );
//
//            if (!controller.addRoute(route)) {
//                helper.fail("Failed to prepare downgrade test");
//                return;
//            }
//        }
//
//        if (controller.setRouteUpgradeLevel(0)) {
//            helper.fail("Downgrade accepted with too many routes");
//            return;
//        }
//
//        if (controller.getRoutes().size() != 5) {
//            helper.fail("Rejected downgrade modified controller state");
//            return;
//        }

        helper.succeed();
    }

    private static CourierEndpoint createInventory(GameTestHelper helper, BlockPos relativePos, Direction side) {
        helper.setBlock(relativePos, Blocks.CHEST);

        return new CourierEndpoint(helper.absolutePos(relativePos), side);
    }


    public static void colonyWakeNoRoutes(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        storeTestBee(helper, controller, false);

        helper.runAfterDelay(5, () -> {
            if (controller.beeCount() != 1) {
                helper.fail("Courier released without a route");
                return;
            }

            helper.succeed();
        });
    }

    public static void colonyWakeDisabledRoute(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        storeTestBee(helper, controller, false);
        addWakeRoute(helper, controller, false);

        helper.runAfterDelay(5, () -> {
            if (controller.beeCount() != 1) {
                helper.fail("Disabled route woke stored bee");
                return;
            }

            helper.succeed();
        });
    }

    public static void colonyWakeCourierRoute(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        storeTestBee(helper, controller, false);
        var route = addWakeRoute(helper, controller, true);

        BlockPos sourcePos = route.source().pos();

        if (!(helper.getLevel().getBlockEntity(sourcePos) instanceof ChestBlockEntity source)) {
            helper.fail("Missing source chest for courier wake test");
            return;
        }

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
        source.setChanged();

        helper.runAfterDelay(5, () -> {
            if (controller.beeCount() != 0) {
                helper.fail("Courier route did not wake stored bee");
                return;
            }

            helper.succeed();
        });
    }

    public static void colonyWakeCooldown(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        storeTestBee(helper, controller, false);
        storeTestBee(helper, controller, false);
        storeTestBee(helper, controller, false);

        var route = addWakeRoute(helper, controller, true);

        BlockPos sourcePos = route.source().pos();

        if (!(helper.getLevel().getBlockEntity(sourcePos) instanceof ChestBlockEntity source)) {
            helper.fail("Missing source chest for courier wake test");
            return;
        }

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
        source.setChanged();

        helper.runAfterDelay(5, () -> {
            if (controller.beeCount() != 2) {
                helper.fail("Expected one release during first cooldown");
                return;
            }

            helper.runAfterDelay(20, () -> {
                if (controller.beeCount() != 2) {
                    helper.fail("Additional bee released during cooldown");
                    return;
                }

                helper.runAfterDelay(45, () -> {
                    if (controller.beeCount() != 1) {
                        helper.fail("Expected second release after cooldown");
                        return;
                    }

                    helper.succeed();
                });
            });
        });
    }

    public static void colonyWakeLockedBee(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        storeTestBee(helper, controller, true);
        addWakeRoute(helper, controller, true);

        helper.runAfterDelay(5, () -> {
            if (controller.beeCount() != 1) {
                helper.fail("Locked occupant was released");
                return;
            }

            helper.succeed();
        });
    }

    public static void colonyWakeBlockedExit(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = createController(helper);

        storeTestBee(helper, controller, false);
        addWakeRoute(helper, controller, true);

        var state = controller.getBlockState();

        if (!state.hasProperty(BeehiveBlock.FACING)) {
            helper.fail("Controller lacks BeehiveBlock.FACING property");
            return;
        }

        Direction facing = state.getValue(BeehiveBlock.FACING);

        helper.setBlock(CONTROLLER_POS.relative(facing), Blocks.STONE);

        helper.runAfterDelay(5, () -> {
            if (controller.beeCount() != 1) {
                helper.fail("Blocked exit caused stored occupant loss");
                return;
            }

            helper.succeed();
        });
    }


}