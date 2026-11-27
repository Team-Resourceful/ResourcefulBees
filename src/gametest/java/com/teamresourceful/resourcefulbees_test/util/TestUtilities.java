package com.teamresourceful.resourcefulbees_test.util;

import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;

public class TestUtilities {

    private static final Identifier TEST_BEE_ID = Identifier.fromNamespaceAndPath("resourcefulbees_test", "gametest_bee");

    public static ResourcefulBee createSpawnedTestBee(GameTestHelper helper) {
        ResourcefulBee bee = createUnspawnedTestBee(helper);

        BlockPos pos = helper.absolutePos(new BlockPos(4, 3, 4));
        bee.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        if (!helper.getLevel().addFreshEntity(bee)) {
            throw new IllegalStateException("Failed to spawn ResourcefulBee for GameTest");
        }

        return bee;
    }

    public static ResourcefulBee createUnspawnedTestBee(GameTestHelper helper) {
        ResourcefulBee bee = createBee(helper);

        if (bee == null) {
            helper.fail("Failed to construct test bee");
            throw new IllegalStateException("Missing GameTest bee: " + TEST_BEE_ID);
        }

        helper.assertTrue(bee.hasCourierAbility(), "GameTest bee should have the COURIER ability");
        helper.assertTrue(bee.getCourierAbility() != null, "GameTest bee should resolve CourierAbility");

        return bee;
    }

    private static ResourcefulBee createBee(GameTestHelper helper) {
        var entry = ModEntities.getModBees().get(TEST_BEE_ID);

        if (entry == null) {
            helper.fail("Missing GameTest bee: " + TEST_BEE_ID);
            throw new IllegalStateException("Missing GameTest bee: " + TEST_BEE_ID);
        }

        CustomBeeEntityType<ResourcefulBee> type = entry.get();

        return type.create(helper.getLevel(), EntitySpawnReason.COMMAND);
    }
}
