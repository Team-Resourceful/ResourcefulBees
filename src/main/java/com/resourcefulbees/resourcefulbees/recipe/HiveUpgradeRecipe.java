package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModRecipeSerializers;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HiveUpgradeRecipe extends ShapedRecipe {

    public HiveUpgradeRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, "", 3, 3, ingredients, result);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInventory inventory) {
        List<ItemStack> stacks = getHives(inventory);
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(inventory);
        ItemStack beeBox = new ItemStack(ModItems.CRAFTING_BEE_BOX.get());
        CompoundNBT tag = new CompoundNBT();
        ListNBT allBees = new ListNBT();
        for (ItemStack stack : stacks) {
            if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("BlockEntityTag"))
                return remainingItems;
            ListNBT list = stack.getTag().getCompound("BlockEntityTag").getList(NBTConstants.NBT_BEES, 10);
            if (!list.isEmpty()) {
                allBees.addAll(list);
            }
        }
        if (!allBees.isEmpty()) {
            tag.put(NBTConstants.NBT_BEES, allBees);
            beeBox.setTag(tag);
            for (int i = 0; i < remainingItems.size(); i++) {
                if (remainingItems.get(i).getItem() == Items.AIR) {
                    remainingItems.set(i, beeBox);
                    break;
                }
            }
        }
        return remainingItems;
    }

    public List<ItemStack> getHives(CraftingInventory inventory) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);
            Block block = Block.byItem(item.getItem());
            if (block == Blocks.AIR) continue;
            if (block instanceof ApiaryBlock || block instanceof BeehiveBlock) {
                stacks.add(item);
            }
        }
        return stacks;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.APIARY_UPGRADE_RECIPE.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        @Override
        public @NotNull ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ShapedRecipe recipe = super.fromJson(recipeId, json);
            return new HiveUpgradeRecipe(recipeId, recipe.getIngredients(), recipe.getResultItem());
        }

        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            assert recipe != null : "why is recipe null?";
            return new HiveUpgradeRecipe(recipeId, recipe.getIngredients(), recipe.getResultItem());
        }
    }
}
