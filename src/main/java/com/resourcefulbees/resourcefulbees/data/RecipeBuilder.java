package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class RecipeBuilder implements IResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    //TODO Needs to be replaced with new registry objects.
    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        BeeRegistry.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb()) {
                if (!customBeeData.CentrifugeData.hasCentrifugeOutput()) {
                    if (Config.CENTRIFUGE_RECIPES.get()) {
                        IRecipe<?> honeycombCentrifuge = this.centrifugeRecipe(s);
                        IRecipe<?> honeycombBlockCentrifuge = this.centrifugeHoneyCombBlockRecipe(s);
                        getRecipeManager().recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                        getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
                    }
                }
                if (Config.HONEYCOMB_BLOCK_RECIPES.get()) {
                    IRecipe<?> honeycombBlock = this.makeHoneycombRecipe(s);
                    IRecipe<?> honeycomb = this.blockToHoneycombRecipe(s);
                    getRecipeManager().recipes.computeIfAbsent(honeycombBlock.getType(), t -> new HashMap<>()).put(honeycombBlock.getId(), honeycombBlock);
                    getRecipeManager().recipes.computeIfAbsent(honeycomb.getType(), t -> new HashMap<>()).put(honeycomb.getId(), honeycomb);
                }
            }
        }));
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        recipeManager = event.getDataPackRegistries().getRecipeManager();
    }

    private IRecipe<?> makeHoneycombRecipe(String beeType) {
        ItemStack honeycombOutput = new ItemStack(BeeRegistry.getBeeData(beeType).getCombBlockItemRegistryObject().get());
        Ingredient honeycombItem = Ingredient.fromItems(BeeRegistry.getBeeData(beeType).getCombRegistryObject().get());

        NonNullList<Ingredient> inputs = NonNullList.from(Ingredient.EMPTY,
                honeycombItem, honeycombItem, honeycombItem,
                honeycombItem, honeycombItem, honeycombItem,
                honeycombItem, honeycombItem, honeycombItem
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block");

        return new ShapedRecipe(name, "", 3, 3, inputs, honeycombOutput);
    }

    private IRecipe<?> centrifugeRecipe(String beeType) {
        CustomBeeData info = BeeRegistry.getBeeData(beeType);
        ItemStack honeycombItemStack = new ItemStack(info.getCombRegistryObject().get(), info.CentrifugeData.getMainInputCount());
        Ingredient honeycombItem = Ingredient.fromStacks(honeycombItemStack);

        NonNullList<Pair<ItemStack,Double>> outputs = NonNullList.from(
                Pair.of(ItemStack.EMPTY, 0.0),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getMainOutput()), info.CentrifugeData.getMainOutputCount()), info.CentrifugeData.getMainOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getSecondaryOutput()), info.CentrifugeData.getSecondaryOutputCount()), info.CentrifugeData.getSecondaryOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getBottleOutput()), info.CentrifugeData.getBottleOutputCount()), info.CentrifugeData.getBottleOutputWeight())
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_centrifuge");

        return new CentrifugeRecipe(name,honeycombItem,outputs, Config.CENTRIFUGE_RECIPE_TIME.get() * 20, false);
    }

    private IRecipe<?> centrifugeHoneyCombBlockRecipe(String beeType) {
        CustomBeeData info = BeeRegistry.getBeeData(beeType);
        ItemStack honeycombBlockItemStack = new ItemStack(info.getCombRegistryObject().get(), 1);
        Ingredient honeycombblockItem = Ingredient.fromStacks(honeycombBlockItemStack);

        NonNullList<Pair<ItemStack,Double>> outputs = NonNullList.from(
                Pair.of(ItemStack.EMPTY, 0.0),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getMainOutput()), info.CentrifugeData.getMainOutputCount() * 9), info.CentrifugeData.getMainOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getSecondaryOutput()), info.CentrifugeData.getSecondaryOutputCount() * 9), info.CentrifugeData.getSecondaryOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getBottleOutput()), info.CentrifugeData.getBottleOutputCount() * 9), info.CentrifugeData.getBottleOutputWeight())
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block_centrifuge");

        return new CentrifugeRecipe(name,honeycombblockItem,outputs, Config.CENTRIFUGE_RECIPE_TIME.get() * 20, true);
    }

    private IRecipe<?> blockToHoneycombRecipe(String beeType) {
        ItemStack honeycombItemStack = new ItemStack(BeeRegistry.getBeeData(beeType).getCombBlockItemRegistryObject().get());
        ItemStack honeycombOutput = new ItemStack(BeeRegistry.getBeeData(beeType).getCombRegistryObject().get(), 9);
        Ingredient honeycombItem = Ingredient.fromStacks(honeycombItemStack);

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_block_to_honeycomb");

        return new ShapelessRecipe(name, "",honeycombOutput, NonNullList.from(Ingredient.EMPTY, honeycombItem));
    }

    public static RecipeManager getRecipeManager() {
        if (!recipeManager.recipes.getClass().equals(HashMap.class)) {
            recipeManager.recipes = new HashMap<>(recipeManager.recipes);
            recipeManager.recipes.replaceAll((t, v) -> new HashMap<>(recipeManager.recipes.get(t)));
        }

        return recipeManager;
    }
}
