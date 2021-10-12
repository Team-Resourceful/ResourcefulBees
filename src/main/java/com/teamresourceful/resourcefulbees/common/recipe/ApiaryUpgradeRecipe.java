package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ApiaryUpgradeRecipe extends ShapedRecipe {

    public ApiaryUpgradeRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, group, width, height, ingredients, result);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInventory inventory) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(inventory);

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);
            Block block = Block.byItem(item.getItem());
            if (block instanceof ApiaryBlock || block instanceof BeehiveBlock) {
                ItemStack box = getBeeBoxFromHive(item);
                if (box == null) continue;
                remainingItems.set(i, box);
            }
        }

        return remainingItems;
    }

    private ItemStack getBeeBoxFromHive(ItemStack hive){
        if (!hive.hasTag() || hive.getTag() == null || hive.getTag().isEmpty() || !hive.getTag().contains("BlockEntityTag")) return null;
        ListNBT list = hive.getTag().getCompound("BlockEntityTag").getList(NBTConstants.NBT_BEES, 10);
        if (!list.isEmpty()){
            ItemStack box = ModItems.CRAFTING_BEE_BOX.get().getDefaultInstance();
            box.getOrCreateTag().put(NBTConstants.NBT_BEES, list);
            return box;
        }
        return null;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.APIARY_UPGRADE_RECIPE.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        @Override
        public @NotNull ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ShapedRecipe recipe = super.fromJson(recipeId, json);
            return new ApiaryUpgradeRecipe(recipeId, recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem());
        }

        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            assert recipe != null : "why is recipe null?";
            return new ApiaryUpgradeRecipe(recipeId, recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem());
        }
    }
}
