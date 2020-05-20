package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

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
        Map<ResourceLocation, IRecipe<?>> furnaceRecipes = recipeManager.recipes.get(IRecipeType.SMELTING);
        for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()){
            if (bee.getKey().equals(BeeConst.DEFAULT_BEE_TYPE) || bee.getKey().equals(BeeConst.DEFAULT_REMOVE)) {
                ItemStack honeycombBlockItemStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
                ItemStack honeycombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
                CompoundNBT rbNBT = new CompoundNBT();
                CompoundNBT btcNBT = new CompoundNBT();
                btcNBT.putString(BeeConst.NBT_COLOR, "");
                btcNBT.putString(BeeConst.NBT_BEE_TYPE, "");
                rbNBT.put(BeeConst.NBT_ROOT,btcNBT);
                honeycombBlockItemStack.setTag(rbNBT);
                honeycombItemStack.setTag(rbNBT);
                IRecipe<?> honeycombBlockRemoval = this.destroyItemsRecipe(new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get()), "honeycomb_block");
                IRecipe<?> honeycombBlockNbtRemoval = this.destroyItemsRecipe(honeycombBlockItemStack, "honeycomb_block_nbt");
                IRecipe<?> honeycombRemoval = this.destroyItemsRecipe(new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get()), "honeycomb");
                IRecipe<?> honeycombNbtRemoval = this.destroyItemsRecipe(honeycombItemStack, "honeycomb_nbt");
                if (honeycombBlockRemoval != null)
                    furnaceRecipes.put(honeycombBlockRemoval.getId(), honeycombBlockRemoval);
                if (honeycombBlockNbtRemoval != null)
                    furnaceRecipes.put(honeycombBlockNbtRemoval.getId(), honeycombBlockNbtRemoval);
                if (honeycombRemoval != null)
                    furnaceRecipes.put(honeycombRemoval.getId(), honeycombRemoval);
                if (honeycombNbtRemoval != null)
                    furnaceRecipes.put(honeycombNbtRemoval.getId(), honeycombNbtRemoval);
                continue;
            }
            else {
                if (Config.CENTRIFUGE_RECIPES.get()) {
                    IRecipe<?> honeycombCentrifuge = this.centrifugeRecipe(bee.getKey(), bee.getValue().getColor());
                    if (honeycombCentrifuge != null)
                        recipeManager.recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                }
                if (Config.HONEYCOMB_BLOCK_RECIPES.get()) {
                    IRecipe<?> honeycombBlock = this.makeHoneycombRecipe(bee.getKey(), bee.getValue().getColor());
                    IRecipe<?> honeycomb = this.blockToHoneycombRecipe(bee.getKey(), bee.getValue().getColor());
                    if (honeycombBlock != null)
                        recipes.put(honeycombBlock.getId(), honeycombBlock);
                    if (honeycomb != null)
                        recipes.put(honeycomb.getId(), honeycomb);
                }
            }
        }
    }

    private IRecipe<?> destroyItemsRecipe(ItemStack removalItem, String type) {
        Ingredient removedItem = new CustomNBTIngredient(removalItem);
        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID,  type + "_removal");
        return new FurnaceRecipe(name, "", removedItem, new ItemStack(RegistryHandler.GOLD_FLOWER_ITEM.get()), 0, 1);
    }

    private IRecipe<?> makeHoneycombRecipe(String BeeType, String Color) {
        ItemStack honeycombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        ItemStack honeycombOutput = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());

        CompoundNBT rbNBT = new CompoundNBT();
        CompoundNBT btcNBT = new CompoundNBT();
        btcNBT.putString(BeeConst.NBT_COLOR, Color);
        btcNBT.putString(BeeConst.NBT_BEE_TYPE, BeeType);
        rbNBT.put(BeeConst.NBT_ROOT,btcNBT);

        honeycombItemStack.setTag(rbNBT);
        honeycombOutput.setTag(rbNBT);

        Ingredient honeycombItem = new CustomNBTIngredient(honeycombItemStack);

        NonNullList<Ingredient> inputs = NonNullList.from(Ingredient.EMPTY,
                honeycombItem, honeycombItem, honeycombItem,
                honeycombItem, honeycombItem, honeycombItem,
                honeycombItem, honeycombItem, honeycombItem
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_honeycomb_block");

        return new ShapedRecipe(name, "", 3, 3, inputs, honeycombOutput);
    }

    private IRecipe<?> centrifugeRecipe(String BeeType, String Color) {
        ItemStack honeycombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        final CompoundNBT honeycombItemStackTag = honeycombItemStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
        honeycombItemStackTag.putString(BeeConst.NBT_COLOR, Color);
        honeycombItemStackTag.putString(BeeConst.NBT_BEE_TYPE, BeeType);

        Ingredient honeycombItem = new CustomNBTIngredient(honeycombItemStack);

        NonNullList<Pair<ItemStack,Double>> outputs = NonNullList.from(
                Pair.of(ItemStack.EMPTY, 0.0),
                Pair.of(new ItemStack(Registry.ITEM.getOrDefault(new ResourceLocation(BeeInfo.getInfo(BeeType).getMainOutput()))), BeeInfo.getInfo(BeeType).getMainOutputWeight()),
                Pair.of(new ItemStack(Registry.ITEM.getOrDefault(new ResourceLocation(BeeInfo.getInfo(BeeType).getSecondaryOutput()))), BeeInfo.getInfo(BeeType).getSecondaryOutputWeight()),
                Pair.of(new ItemStack(Registry.ITEM.getOrDefault(new ResourceLocation(BeeInfo.getInfo(BeeType).getBottleOutput()))),BeeInfo.getInfo(BeeType).getBottleOutputWeight())
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_honeycomb_centrifuge");

        return new CentrifugeRecipe(name,honeycombItem,outputs,Config.CENTRIFUGE_RECIPE_TIME.get() * 20);
    }

    private IRecipe<?> blockToHoneycombRecipe(String BeeType, String Color) {
        ItemStack honeycombItemStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        ItemStack honeycombOutput = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());

        CompoundNBT rbNBT = new CompoundNBT();
        CompoundNBT btcNBT = new CompoundNBT();
        btcNBT.putString(BeeConst.NBT_COLOR, Color);
        btcNBT.putString(BeeConst.NBT_BEE_TYPE, BeeType);
        rbNBT.put(BeeConst.NBT_ROOT,btcNBT);

        honeycombItemStack.setTag(rbNBT);
        honeycombOutput.setTag(rbNBT);
        honeycombOutput.setCount(9);

        Ingredient honeycombItem = new CustomNBTIngredient(honeycombItemStack);

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_block_to_honeycomb");

        return new ShapelessRecipe(name, "",honeycombOutput, NonNullList.from(Ingredient.EMPTY, honeycombItem));
    }


    private static class CustomNBTIngredient extends NBTIngredient
    {
        public CustomNBTIngredient(ItemStack stack)
        {
            super(stack);
        }
    }
}
