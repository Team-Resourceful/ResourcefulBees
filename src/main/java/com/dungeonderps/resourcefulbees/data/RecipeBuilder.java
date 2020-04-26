package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;

public class RecipeBuilder implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        RecipeManager recipeManager = server.getRecipeManager();
        recipeManager.recipes = new HashMap<>(recipeManager.recipes);
        recipeManager.recipes.replaceAll((t, v) -> new HashMap<>(recipeManager.recipes.get(t)));
        Map<ResourceLocation, IRecipe<?>> recipes = recipeManager.recipes.get(IRecipeType.CRAFTING);
        for (Map.Entry<String, BeeInfo> beeType : CustomBeeEntity.BEE_INFO.entrySet()){
            if (beeType.getKey() == "Default")
                continue;
            else {
                IRecipe<?> honeyComb = this.makeHoneyCombRecipe(beeType.getKey(), beeType.getValue().getColor());
                if (honeyComb != null)
                    recipes.put(honeyComb.getId(), honeyComb);
            }
        }
    }
    private IRecipe<?> makeHoneyCombRecipe(String BeeType, String Color) {
        ItemStack honeyCombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        final CompoundNBT honeyCombItemStackTag = honeyCombItemStack.getOrCreateChildTag("ResourcefulBees");
        honeyCombItemStackTag.putString("Color", Color);
        honeyCombItemStackTag.putString("BeeType", BeeType);
        Ingredient honeyCombItem = new CustomNBTIngredient(honeyCombItemStack);
        NonNullList<Ingredient> inputs = NonNullList.from(Ingredient.EMPTY,
                honeyCombItem, honeyCombItem, honeyCombItem,
                honeyCombItem, honeyCombItem, honeyCombItem,
                honeyCombItem, honeyCombItem, honeyCombItem
        );

        final ItemStack honeyCombOutput = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        final CompoundNBT honeyCombBlockItemTag = honeyCombOutput.getOrCreateChildTag("ResourcefulBees");
        honeyCombBlockItemTag.putString("Color", Color);
        honeyCombBlockItemTag.putString("BeeType", BeeType);

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType.toLowerCase() + "_honeycomb");

        return new ShapedRecipe(name, "", 3, 3, inputs, honeyCombOutput);
    }

    private static class CustomNBTIngredient extends NBTIngredient
    {
        public CustomNBTIngredient(ItemStack stack)
        {
            super(stack);
        }
    }
}
