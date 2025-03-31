package com.teamresourceful.resourcefulbees.common.subsystems;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface JeiSubsystem {

    default void addRecipeCatalysts(RecipeCatalystsEvent event) {

    }

    default void addExtraInfo(ExtraInfoEvent event) {

    }

    default void addScreenHandlers(ScreenHandlersEvent event) {

    }

    @FunctionalInterface
    interface RecipeCatalystsEvent {
        void add(ItemStack ingredient, RecipeType<?>... recipeTypes);

        default void add(List<ItemStack> stacks, RecipeType<?>... recipeTypes) {
            stacks.forEach(stack -> add(stack, recipeTypes));
        }
    }

    @FunctionalInterface
    interface ScreenHandlersEvent {

        <T extends Screen> void addGhostHandler(Class<T> guiClass, IGhostIngredientHandler<T> handler);
    }

    @FunctionalInterface
    interface ExtraInfoEvent {
        void addExtraInfo(ItemStack item, Component... text);

        default void addExtraInfo(List<ItemStack> stacks, Component... text) {
            stacks.forEach(stack -> addExtraInfo(stack, text));
        }
    }
}
