package com.resourcefulbees.resourcefulbees.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class LazyLoadedTagIngredient extends Ingredient {

    private final ITag<Item> tag;

    private ItemStack[] stacks;
    private IntList stackingIds;

    public LazyLoadedTagIngredient(ITag<Item> tag) {
        super(Stream.empty());
        this.tag = tag;
    }

    private void updateStacks() {
        if (stacks != null) return;
        stacks = tag.getValues().stream().map(ItemStack::new).toArray(ItemStack[]::new);
    }

    @Override
    public @NotNull ItemStack[] getItems() {
        updateStacks();
        return stacks;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        updateStacks();
        if (stacks.length == 0) return stack.isEmpty();
        return tag.contains(stack.getItem());
    }

    @Override
    public @NotNull
    IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.updateStacks();
            this.stackingIds = new IntArrayList(getItems().length);

            for(ItemStack itemstack : getItems()) {
                this.stackingIds.add(RecipeItemHelper.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    @Override
    public boolean isEmpty() {
        return getItems().length == 0;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return VanillaIngredientSerializer.INSTANCE;
    }
}
