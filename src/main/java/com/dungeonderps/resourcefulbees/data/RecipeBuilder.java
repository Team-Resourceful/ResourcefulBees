package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RecipeBuilder implements ISelectiveResourceReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        RecipeManager recipeManager = server.getRecipeManager();
        recipeManager.recipes = new HashMap<>(recipeManager.recipes);
        recipeManager.recipes.replaceAll((t, v) -> new HashMap<>(recipeManager.recipes.get(t)));
        Map<ResourceLocation, IRecipe<?>> recipes = recipeManager.recipes.get(IRecipeType.CRAFTING);
        for (Map.Entry<String, BeeInfo> beeType : BeeInfo.BEE_INFO.entrySet()){
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
        final CompoundNBT honeyCombItemStackTag = honeyCombItemStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
        honeyCombItemStackTag.putString(BeeConst.NBT_COLOR, Color);
        honeyCombItemStackTag.putString(BeeConst.NBT_BEE_TYPE, BeeType);
        Ingredient honeyCombItem = new CustomNBTIngredient(honeyCombItemStack);
        NonNullList<Ingredient> inputs = NonNullList.from(Ingredient.EMPTY,
                honeyCombItem, honeyCombItem, honeyCombItem,
                honeyCombItem, honeyCombItem, honeyCombItem,
                honeyCombItem, honeyCombItem, honeyCombItem
        );

        final ItemStack honeyCombOutput = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        final CompoundNBT honeyCombBlockItemTag = honeyCombOutput.getOrCreateChildTag(BeeConst.NBT_ROOT);
        honeyCombBlockItemTag.putString(BeeConst.NBT_COLOR, Color);
        honeyCombBlockItemTag.putString(BeeConst.NBT_BEE_TYPE, BeeType);

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
