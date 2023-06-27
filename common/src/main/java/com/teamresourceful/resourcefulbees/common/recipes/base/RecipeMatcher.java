package com.teamresourceful.resourcefulbees.common.recipes.base;

import net.minecraft.world.Container;

/**
 * This interface is used to check if a recipe matches an inventory
 * Similar to {@link net.minecraft.world.item.crafting.Recipe#matches(Container, net.minecraft.world.level.Level)}
 * but without the level parameter
 */
public interface RecipeMatcher {

    /**
     * Checks if the recipe matches the inventory
     * @param inventory the inventory to check
     * @return true if the recipe matches the inventory
     */
    boolean matches(Container inventory);
}
