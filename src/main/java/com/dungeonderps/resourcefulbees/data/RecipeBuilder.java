package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
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

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class RecipeBuilder implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        RecipeManager recipeManager = server.getRecipeManager();
        recipeManager.recipes = new HashMap<>(recipeManager.recipes);
        recipeManager.recipes.replaceAll((t, v) -> new HashMap<>(recipeManager.recipes.get(t)));
        Map<ResourceLocation, IRecipe<?>> recipes = recipeManager.recipes.get(IRecipeType.CRAFTING);
        for (Map.Entry<String, BeeInfo> beeType : BeeInfo.BEE_INFO.entrySet()){
            if (beeType.getKey() == "Default")
                continue;
            else {
                IRecipe<?> honeyCombBlock = this.makeHoneyCombRecipe(beeType.getKey(), beeType.getValue().getColor());
                IRecipe<?> honeyComb = this.blockToHoneyCombRecipe(beeType.getKey(), beeType.getValue().getColor());
                if (honeyCombBlock != null)
                    recipes.put(honeyCombBlock.getId(), honeyCombBlock);
                if (honeyComb != null)
                    recipes.put(honeyComb.getId(), honeyComb);
            }
        }
    }

    private IRecipe<?> makeHoneyCombRecipe(String BeeType, String Color) {
        ItemStack honeyCombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        ItemStack honeyCombOutput = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());

        CompoundNBT rbNBT = new CompoundNBT();
        CompoundNBT btcNBT = new CompoundNBT();
        btcNBT.putString(BeeConst.NBT_COLOR, Color);
        btcNBT.putString(BeeConst.NBT_BEE_TYPE, BeeType);
        rbNBT.put(BeeConst.NBT_ROOT,btcNBT);

        honeyCombItemStack.setTag(rbNBT);
        honeyCombOutput.setTag(rbNBT);

        Ingredient honeyCombItem = new CustomNBTIngredient(honeyCombItemStack);

        NonNullList<Ingredient> inputs = NonNullList.from(Ingredient.EMPTY,
                honeyCombItem, honeyCombItem, honeyCombItem,
                honeyCombItem, honeyCombItem, honeyCombItem,
                honeyCombItem, honeyCombItem, honeyCombItem
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType.toLowerCase() + "_honeycomb_block");

        return new ShapedRecipe(name, "", 3, 3, inputs, honeyCombOutput);
    }

    private IRecipe<?> blockToHoneyCombRecipe(String BeeType, String Color) {
        ItemStack honeyCombItemStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        ItemStack honeyCombOutput = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());

        CompoundNBT rbNBT = new CompoundNBT();
        CompoundNBT btcNBT = new CompoundNBT();
        btcNBT.putString(BeeConst.NBT_COLOR, Color);
        btcNBT.putString(BeeConst.NBT_BEE_TYPE, BeeType);
        rbNBT.put(BeeConst.NBT_ROOT,btcNBT);

        honeyCombItemStack.setTag(rbNBT);
        honeyCombOutput.setTag(rbNBT);
        honeyCombOutput.setCount(9);

        Ingredient honeyCombItem = new CustomNBTIngredient(honeyCombItemStack);

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType.toLowerCase() + "_block_to_honeycomb");

        return new ShapelessRecipe(name, "",honeyCombOutput, NonNullList.from(Ingredient.EMPTY, honeyCombItem));
    }


    private static class CustomNBTIngredient extends NBTIngredient
    {
        public CustomNBTIngredient(ItemStack stack)
        {
            super(stack);
        }
    }
}
