package com.teamresourceful.resourcefulbees_test.block;

import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.ApiaryOccupant;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.mixin.BeeAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ApiaryBlockEntityTests {

    private static final BlockPos APIARY_POS = new BlockPos(1, 1, 1);

    private static ApiaryBlockEntity createApiary(GameTestHelper helper) {
        helper.setBlock(APIARY_POS, ModBlocks.T1_APIARY_BLOCK.get());

        return helper.getBlockEntity(APIARY_POS, ApiaryBlockEntity.class);
    }

    private static Bee spawnBee(GameTestHelper helper) {
        return helper.spawn(EntityTypes.BEE, APIARY_POS.above());
    }

    private static Bee addBee(GameTestHelper helper, ApiaryBlockEntity apiary) {
        Bee bee = spawnBee(helper);
        apiary.tryEnterHive(bee);
        return bee;
    }

    private static int countLiveBees(GameTestHelper helper, ApiaryBlockEntity apiary) {
        AABB area = new AABB(apiary.getBlockPos()).inflate(1.0);

        return helper.getLevel().getEntitiesOfClass(Bee.class, area).size();
    }

    private static ApiaryBlockEntity reloadApiary(GameTestHelper helper, ApiaryBlockEntity original) {
        var level = helper.getLevel();
        BlockPos pos = original.getBlockPos();
        BlockState state = original.getBlockState();

        CompoundTag saved = original.saveWithFullMetadata(level.registryAccess());

        BlockEntity loaded = BlockEntity.loadStatic(pos, state, saved, level.registryAccess());

        helper.assertTrue(loaded instanceof ApiaryBlockEntity, "Apiary should deserialize successfully");

        level.removeBlockEntity(pos);
        level.setBlockEntity(loaded);

        return (ApiaryBlockEntity) loaded;
    }

    // Test 1: A bee enters the apiary.

    public static void beeEntersApiary(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        helper.assertTrue(apiary.beeCount() == 0, "New apiary should be empty");

        Bee bee = addBee(helper, apiary);

        helper.assertTrue(apiary.beeCount() == 1, "Apiary should store one bee");

        helper.assertTrue(bee.isRemoved(), "Stored bee should be removed from the world");

        helper.assertTrue(countLiveBees(helper, apiary) == 0, "No live bee should remain outside");

        helper.succeed();
    }

    // Test 2: Capacity is enforced.

    public static void apiaryRespectsCapacity(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        int capacity = apiary.getTier().maxBees();

        for (int i = 0; i < capacity; i++) {
            Bee bee = addBee(helper, apiary);

            helper.assertTrue(bee.isRemoved(), "Bee should enter while space remains");
        }

        helper.assertTrue(!apiary.hasSpace(), "Full apiary should report no available space");

        helper.assertTrue(apiary.beeCount() == capacity, "Apiary should reach its configured capacity");

        Bee extraBee = spawnBee(helper);
        apiary.tryEnterHive(extraBee);

        helper.assertTrue(apiary.beeCount() == capacity, "Apiary must not exceed capacity");

        helper.assertTrue(!extraBee.isRemoved(), "Rejected bee should remain in the world");

        helper.succeed();
    }

    // Test 3: Release creates a live bee.

    public static void apiaryBeeRelease(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        addBee(helper, apiary);

        helper.assertTrue(apiary.beeCount() == 1, "Apiary should contain one stored bee");

        int liveBees = countLiveBees(helper, apiary);

        helper.assertTrue(liveBees == 0, "Expected 0 live bees before release, found " + liveBees);

        // Determine which direction the apiary faces.
        Direction facing = apiary.getBlockState().getValue(BeehiveBlock.FACING);

        // Clear the block directly in front of the apiary.
        BlockPos exitPos = apiary.getBlockPos().relative(facing);

        helper.getLevel().setBlockAndUpdate(exitPos, Blocks.AIR.defaultBlockState());

        // Verify the release precondition.
        helper.assertTrue(helper.getLevel().getBlockState(exitPos).getCollisionShape(helper.getLevel(), exitPos).isEmpty(), "Apiary exit must be unobstructed");

        ApiaryOccupant occupant = apiary.getBees().getFirst();

        boolean released = apiary.releaseBee(apiary.getBlockState(), occupant);

        helper.assertTrue(released, "Apiary should successfully release the bee");

        helper.assertTrue(countLiveBees(helper, apiary) == 1, "Release should spawn exactly one bee");

        helper.assertTrue(apiary.beeCount() == 1, "Direct release should not remove stored occupant");

        helper.succeed();
    }

    // Test 4: Stored bees survive serialization.

    public static void apiaryOccupantsSurviveReload(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        addBee(helper, apiary);
        addBee(helper, apiary);

        helper.assertTrue(apiary.beeCount() == 2, "Apiary should contain two bees before reload");

        ApiaryBlockEntity reloaded = reloadApiary(helper, apiary);

        helper.assertTrue(reloaded != apiary, "Reload should create a new block entity");

        helper.assertTrue(reloaded.beeCount() == 2, "Reloaded apiary should retain both bees");

        helper.assertTrue(reloaded.hasSpace(), "Reloaded apiary should still have available space");

        helper.succeed();
    }


    private static BlockPos exitPos(ApiaryBlockEntity apiary) {
        Direction facing = apiary.getBlockState().getValue(BeehiveBlock.FACING);

        return apiary.getBlockPos().relative(facing);
    }

    private static void setExitBlocked(GameTestHelper helper, ApiaryBlockEntity apiary, boolean blocked) {
        helper.getLevel().setBlockAndUpdate(exitPos(apiary), blocked ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState());
    }

    private static void insertOutput(ApiaryBlockEntity apiary, ItemStack stack) {
        try (Transaction transaction = Transaction.openRoot()) {
            apiary.resourceHandler().insertOutput(ItemResource.of(stack), stack.getCount(), transaction);

            transaction.commit();
        }
    }

    private static int countOutput(ApiaryBlockEntity apiary, ItemResource resource) {
        int count = 0;

        var handler = apiary.resourceHandler();

        // Slot 0 is reserved; outputs begin at slot 1.
        for (int slot = 1; slot < handler.size(); slot++) {
            if (handler.getResource(slot).equals(resource)) {
                count += handler.getAmountAsInt(slot);
            }
        }

        return count;
    }

    public static void apiaryDoesNotReleaseThroughBlockedExit(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        addBee(helper, apiary);

        setExitBlocked(helper, apiary, true);

        helper.assertTrue(!helper.getLevel().getBlockState(exitPos(apiary)).getCollisionShape(helper.getLevel(), exitPos(apiary)).isEmpty(), "Apiary exit should be obstructed");

        ApiaryOccupant occupant = apiary.getBees().getFirst();

        boolean released = apiary.releaseBee(apiary.getBlockState(), occupant);

        helper.assertTrue(!released, "Apiary should reject release through a blocked exit");

        helper.assertTrue(apiary.beeCount() == 1, "Bee should remain stored");

        helper.assertTrue(countLiveBees(helper, apiary) == 0, "No live bee should be spawned");

        helper.succeed();
    }

    public static void apiaryAcceptsResourceOutput(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        ItemStack output = new ItemStack(Items.HONEYCOMB, 5);
        ItemResource resource = ItemResource.of(output);

        helper.assertTrue(countOutput(apiary, resource) == 0, "New apiary should have no honeycombs");

        insertOutput(apiary, output);

        helper.assertTrue(countOutput(apiary, resource) == 5, "Apiary should store five honeycombs");

        // Insert a second batch of the same resource.
        insertOutput(apiary, new ItemStack(Items.HONEYCOMB, 3));

        helper.assertTrue(countOutput(apiary, resource) == 8, "Compatible output should accumulate");

        helper.succeed();
    }

    public static void apiaryOutputSurvivesReload(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        insertOutput(apiary, new ItemStack(Items.HONEYCOMB, 12));

        insertOutput(apiary, new ItemStack(Items.GOLD_INGOT, 4));

        ApiaryBlockEntity reloaded = reloadApiary(helper, apiary);

        helper.assertTrue(reloaded != apiary, "Reload should create a new block entity");

        helper.assertTrue(countOutput(reloaded, ItemResource.of(Items.HONEYCOMB)) == 12, "Honeycomb output should survive reload");

        helper.assertTrue(countOutput(reloaded, ItemResource.of(Items.GOLD_INGOT)) == 4, "Gold ingot output should survive reload");

        helper.succeed();
    }

    public static void apiaryProducesHoneycombFromNectar(GameTestHelper helper) {
        ApiaryBlockEntity apiary = createApiary(helper);

        // Create a bee carrying nectar.
        Bee bee = spawnBee(helper);

        ((BeeAccessor) bee).resourcefulbees$invokeSetHasNectar(true);

        helper.assertTrue(bee.hasNectar(), "Test bee should be carrying nectar");

        // Determine the expected recipe output.
        ItemStack expected = HiveRecipe.getApiaryOutput(apiary.getTier(), bee).orElse(ItemStack.EMPTY);

        helper.assertTrue(!expected.isEmpty(), "Bee should have a valid apiary output");

        ItemResource resource = ItemResource.of(expected);

        int initialOutput = countOutput(apiary, resource);

        // Store the bee in the apiary.
        apiary.tryEnterHive(bee);

        helper.assertTrue(apiary.beeCount() == 1, "Nectar-carrying bee should enter the apiary");

        helper.assertTrue(apiary.getBees().getFirst().hasNectar(), "Stored occupant should retain nectar state");

        // Allow the bee to leave.
        setExitBlocked(helper, apiary, false);

        ApiaryOccupant occupant = apiary.getBees().getFirst();

        boolean released = apiary.releaseBee(apiary.getBlockState(), occupant);

        helper.assertTrue(released, "Nectar-carrying bee should be released");

        helper.assertTrue(countOutput(apiary, resource) == initialOutput + expected.getCount(), "Nectar delivery should insert the expected output");

        helper.assertTrue(countLiveBees(helper, apiary) == 1, "One bee should exist after release");

        helper.succeed();
    }
}
