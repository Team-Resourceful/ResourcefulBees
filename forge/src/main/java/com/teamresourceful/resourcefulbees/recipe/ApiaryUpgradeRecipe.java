package com.teamresourceful.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.registry.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ApiaryUpgradeRecipe extends ShapedRecipe {

    public ApiaryUpgradeRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, "", 3, 3, ingredients, result);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer inventory) {
        List<ItemStack> stacks = getHives(inventory);
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(inventory);
        ItemStack beeBox = new ItemStack(ModItems.CRAFTING_BEE_BOX.get());
        CompoundTag tag = new CompoundTag();
        ListTag allBees = new ListTag();
        for (ItemStack stack : stacks) {
            if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("BlockEntityTag"))
                return remainingItems;
            ListTag list = stack.getTag().getCompound("BlockEntityTag").getList(NBTConstants.NBT_BEES, 10);
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

    public List<ItemStack> getHives(CraftingContainer inventory) {
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
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.APIARY_UPGRADE_RECIPE.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        @Override
        public @NotNull ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ShapedRecipe recipe = super.fromJson(recipeId, json);
            return new ApiaryUpgradeRecipe(recipeId, recipe.getIngredients(), recipe.getResultItem());
        }

        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            assert recipe != null : "why is recipe null?";
            return new ApiaryUpgradeRecipe(recipeId, recipe.getIngredients(), recipe.getResultItem());
        }
    }
}
