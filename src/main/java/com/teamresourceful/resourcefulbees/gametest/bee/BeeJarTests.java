package com.teamresourceful.resourcefulbees.gametest.bee;

import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;

public class BeeJarTests {

    public static void jarStoresBeeOccupant() {

        ItemStack jar = new ItemStack(ModItems.BEE_JAR.get());


        if (BeeJarItem.isFilled(jar)) {
            throw new AssertionError("New bee jar should be empty");
        }

        JarOccupant occupant = JarOccupant.from(EntityTypes.BEE, new Color(0xFFFFD700));

        jar.set(ModDataComponents.JAR_BEE.get(), occupant);

        if (!BeeJarItem.isFilled(jar)) {
            throw new AssertionError("Bee jar should be filled");
        }

        if (BeeJarItem.occupantFrom(jar).entityType() != EntityTypes.BEE) {
            throw new AssertionError(
                    "Bee jar contains the wrong entity type"
            );
        }

        if (BeeJarItem.hasEntityData(jar)) {
            throw new AssertionError(
                    "Display occupant should not have entity data"
            );
        }
    }
}
