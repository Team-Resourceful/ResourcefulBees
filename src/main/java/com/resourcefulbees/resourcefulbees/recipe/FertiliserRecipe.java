package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModRecipeSerializers;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FertiliserRecipe extends ShapelessRecipe {

    public FertiliserRecipe(ResourceLocation id, String group, ItemStack recipeOut, NonNullList<Ingredient> recipeIngredients) {
        super(id, group, recipeOut, recipeIngredients);
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FERTILIZER_RECIPE.get();
    }

    /**
     * Returns an Item that is the result of this recipe Returns the output of the
     * recipe, but with radius NBT equal to smallest among inputs + 1
     */
    @Override
    public ItemStack assemble(CraftingInventory craftingSlots)
    {
        List<ItemStack> stacks = new ArrayList<>();
        int slotCount = craftingSlots.getContainerSize();
        for (int i=0; i<slotCount; i++)
        {
            ItemStack stack = craftingSlots.getItem(i);
            if(stack.getItem().equals(ModItems.POLLEN.get())) {
                stacks.add(craftingSlots.getItem(i));
            }
        }
        return this.getActualOutput(stacks);
    }

    public ItemStack getActualOutput(List<ItemStack> stacks) {
        ItemStack result = this.getResultItem().copy();

        if(stacksAreCustom(stacks)) {
            if(stacks.size() == 3 &&
                    stacks.get(0).getTag().getString("specific").equals(stacks.get(1).getTag().getString("specific"))
                    && stacks.get(2).getTag().getString("specific").equals(stacks.get(1).getTag().getString("specific"))) {
                CompoundNBT compoundNBT = new CompoundNBT();
                compoundNBT.putString("specific", stacks.get(0).getTag().getString("specific"));
                result.setTag(compoundNBT);
            }
        }

        return result;
    }

    public boolean stacksAreCustom(List<ItemStack> stacks) {
        for (ItemStack stack:
             stacks) {
            if(stack == null || stack.getTag() == null || stack.getTag().getString("specific").isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(final CraftingInventory inv) {
        final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remainingItems.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty() && itemstack.getItem().equals(Items.BOWL)) {
                remainingItems.set(i, itemstack.getContainerItem());
            }
        }
        return remainingItems;
    }

    public static class Serializer extends ShapelessRecipe.Serializer
    {
        @Override
        public ShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            ShapelessRecipe recipe = super.fromJson(recipeId, json);

            return new FertiliserRecipe(recipeId, recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
        }

        @Override
        public ShapelessRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            ShapelessRecipe recipe = super.fromNetwork(recipeId, buffer);

            return new FertiliserRecipe(recipeId, recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
        }
    }
}
