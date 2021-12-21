package com.teamresourceful.resourcefulbees.common.ingredients;

import com.teamresourceful.resourcefulbees.api.beedata.itemsholder.IItemHolder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ItemHolderIngredient extends Ingredient implements IAmountSensitive {

    private final IItemHolder holder;
    private final int count;

    private ItemStack[] stacks;
    private IntList stackingIds;

    public ItemHolderIngredient(IItemHolder holder, int count) {
        super(Stream.empty());
        this.holder = holder;
        this.count = count;
    }

    private void updateStacks() {
        if (stacks != null) return;
        stacks = holder.getItems().stream().map(item -> new ItemStack(item, count)).toArray(ItemStack[]::new);
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
        if (stack.getCount() < getAmount()) return false;
        return holder.matches(stack);
    }

    @Override
    public @NotNull IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.updateStacks();
            this.stackingIds = new IntArrayList(getItems().length);

            for(ItemStack itemstack : getItems()) {
                this.stackingIds.add(StackedContents.getStackingIndex(itemstack));
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

    @Override
    public int getAmount() {
        return count;
    }
}
