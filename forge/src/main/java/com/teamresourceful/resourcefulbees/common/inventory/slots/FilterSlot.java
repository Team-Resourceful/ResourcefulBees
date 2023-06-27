package com.teamresourceful.resourcefulbees.common.inventory.slots;

import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeMatcher;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FilterSlot extends SlotItemHandler {

    public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
        //not a real slot
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    //This method and interface exist so that recipes can work with this slot and IAmountSensitive ingredients

    public static <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(RecipeType<T> recipeType, C inventory, Level level) {
       return level.getRecipeManager().getAllRecipesFor(recipeType).stream()
               .filter(t -> t instanceof RecipeMatcher matcher && matcher.matches(inventory))
               .findFirst();
    }
}
