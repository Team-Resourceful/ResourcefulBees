
package com.teamresourceful.resourcefulbees.gametest.block;

import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TieredBeehiveEntityTests {

    private static final BlockPos HIVE_POS = new BlockPos(1, 1, 1);

    private static TieredBeehiveBlockEntity createHive(GameTestHelper helper) {
        TieredBeehiveBlock block = (TieredBeehiveBlock)
                ModBlocks.HIVES.getEntries()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .get();

        helper.setBlock(HIVE_POS, block);

        return helper.getBlockEntity(HIVE_POS, TieredBeehiveBlockEntity.class);
    }

    private static Bee spawnBee(GameTestHelper helper) {
        return helper.spawn(EntityTypes.BEE, HIVE_POS.above());
    }

    private static Bee addBee(GameTestHelper helper, TieredBeehiveBlockEntity hive) {
        Bee bee = spawnBee(helper);
        hive.addOccupant(bee);
        return bee;
    }

    private static TieredBeehiveBlockEntity reloadHive(GameTestHelper helper, TieredBeehiveBlockEntity original) {
        var level = helper.getLevel();
        BlockPos pos = original.getBlockPos();
        BlockState state = original.getBlockState();

        CompoundTag saved = original.saveWithFullMetadata(level.registryAccess());
        BlockEntity loaded = BlockEntity.loadStatic(pos, state, saved, level.registryAccess());

        helper.assertTrue(
                loaded instanceof TieredBeehiveBlockEntity,
                "Saved hive should deserialize successfully"
        );

        level.removeBlockEntity(pos);
        level.setBlockEntity(loaded);

        return (TieredBeehiveBlockEntity) loaded;
    }

    // Test 1: A bee enters the hive.

    public static void beeEntersHive(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        helper.assertTrue(
                hive.getOccupantCount() == 0,
                "New hive should be empty"
        );

        Bee bee = addBee(helper, hive);

        helper.assertTrue(
                hive.getOccupantCount() == 1,
                "Hive should contain one bee"
        );

        helper.assertTrue(
                hive.hasBees(),
                "Hive should report that it contains bees"
        );

        helper.assertTrue(
                bee.isRemoved(),
                "Bee should be removed from the world"
        );

        helper.succeed();
    }

    // Test 2: A full hive rejects additional bees.

    public static void hiveRespectsBeeCapacity(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        TieredBeehiveBlock block =
                (TieredBeehiveBlock) hive.getBlockState()
                        .getBlock();

        int capacity = block.getTier().maxBees();

        for (int i = 0; i < capacity; i++) {
            Bee bee = addBee(helper, hive);

            helper.assertTrue(
                    bee.isRemoved(),
                    "Accepted bee should be removed"
            );
        }

        helper.assertTrue(
                hive.isFull(),
                "Hive should report being full"
        );

        helper.assertTrue(
                hive.getOccupantCount() == capacity,
                "Hive should contain exactly its capacity"
        );

        Bee extraBee = spawnBee(helper);
        hive.addOccupant(extraBee);

        helper.assertTrue(
                hive.getOccupantCount() == capacity,
                "Hive must not exceed its capacity"
        );

        helper.assertTrue(
                !extraBee.isRemoved(),
                "Rejected bee should remain in the world"
        );

        helper.succeed();
    }

    // Test 3: A stored bee is released.

    public static void beeReleasedFromHive(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        addBee(helper, hive);

        helper.assertTrue(
                hive.getOccupantCount() == 1,
                "Hive should initially contain one bee"
        );

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        hive.emptyAllLivingFromHive(
                player,
                hive.getBlockState(),
                BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY
        );

        helper.assertTrue(
                hive.getOccupantCount() == 0,
                "Hive should be empty after emergency release"
        );

        helper.assertTrue(
                !hive.hasBees(),
                "Hive should no longer report stored bees"
        );

        helper.succeed();
    }

    // Test 4: Occupants survive serialization.

    public static void hiveOccupantsSurviveReload(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        addBee(helper, hive);
        addBee(helper, hive);

        helper.assertTrue(
                hive.getOccupantCount() == 2,
                "Hive should contain two bees before reload"
        );

        TieredBeehiveBlockEntity reloaded =
                reloadHive(helper, hive);

        helper.assertTrue(
                reloaded != hive,
                "Reload should create a new block entity"
        );

        helper.assertTrue(
                reloaded.getOccupantCount() == 2,
                "Reloaded hive should retain both bees"
        );

        helper.assertTrue(
                reloaded.hasBees(),
                "Reloaded hive should report stored bees"
        );

        helper.succeed();
    }

    private static int countLiveBees(GameTestHelper helper, TieredBeehiveBlockEntity hive) {
        AABB area = new AABB(hive.getBlockPos()).inflate(5.0);

        return helper.getLevel()
                .getEntitiesOfClass(Bee.class, area)
                .size();
    }

    public static void releasedBeeExistsInWorld(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        Bee bee = addBee(helper, hive);

        helper.assertTrue(
                bee.isRemoved(),
                "Original bee should be stored and removed"
        );

        helper.assertTrue(
                countLiveBees(helper, hive) == 0,
                "No live bees should remain before release"
        );

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        hive.emptyAllLivingFromHive(
                player,
                hive.getBlockState(),
                BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY
        );

        helper.assertTrue(
                hive.getOccupantCount() == 0,
                "Hive should be empty after release"
        );

        helper.assertTrue(
                countLiveBees(helper, hive) == 1,
                "Exactly one live bee should exist after release"
        );

        helper.succeed();
    }

    public static void beeRemainsStoredBeforeRelease(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        addBee(helper, hive);

        helper.assertTrue(
                hive.getOccupantCount() == 1,
                "Hive should initially contain one bee"
        );

        helper.runAfterDelay(100, () -> {
            helper.assertTrue(
                    hive.getOccupantCount() == 1,
                    "Bee should remain stored after 100 ticks"
            );

            helper.assertTrue(
                    countLiveBees(helper, hive) == 0,
                    "Bee should not have spawned outside the hive"
            );

            helper.succeed();
        });
    }
    public static void multipleBeesReleaseCorrectly(GameTestHelper helper) {
        TieredBeehiveBlockEntity hive = createHive(helper);

        addBee(helper, hive);
        addBee(helper, hive);

        helper.assertTrue(
                hive.getOccupantCount() == 2,
                "Hive should contain two stored bees"
        );

        helper.assertTrue(
                countLiveBees(helper, hive) == 0,
                "No bees should exist outside before release"
        );

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        hive.emptyAllLivingFromHive(
                player,
                hive.getBlockState(),
                BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY
        );

        helper.assertTrue(
                hive.getOccupantCount() == 0,
                "All stored bees should be released"
        );

        helper.assertTrue(
                countLiveBees(helper, hive) == 2,
                "Exactly two live bees should exist after release"
        );

        helper.succeed();
    }
}
