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

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        BeeRegistry.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb()) {
                if (customBeeData.CentrifugeData.hasCentrifugeOutput() && Config.CENTRIFUGE_RECIPES.get()) {
                    IRecipe<?> honeycombCentrifuge = this.centrifugeRecipe(s, customBeeData);
                    IRecipe<?> honeycombBlockCentrifuge = this.centrifugeHoneyCombBlockRecipe(s, customBeeData);
                    getRecipeManager().recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                    getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
                }
                if (Config.HONEYCOMB_BLOCK_RECIPES.get()) {
                    IRecipe<?> honeycombBlock = this.makeHoneycombRecipe(s, customBeeData);
                    IRecipe<?> honeycomb = this.combBlockToCombRecipe(s, customBeeData);
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

    private IRecipe<?> makeHoneycombRecipe(String beeType, CustomBeeData info) {
        Ingredient honeycombItem = Ingredient.fromItems(info.getCombRegistryObject().get());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block"),
                "",
                3,
                3,
                NonNullList.from(Ingredient.EMPTY,
                        honeycombItem, honeycombItem, honeycombItem,
                        honeycombItem, honeycombItem, honeycombItem,
                        honeycombItem, honeycombItem, honeycombItem
                ),
                new ItemStack(info.getCombBlockItemRegistryObject().get())
        );
    }

    private IRecipe<?> centrifugeRecipe(String beeType, CustomBeeData info) {
        return new CentrifugeRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_centrifuge"),
                Ingredient.fromStacks(new ItemStack(info.getCombRegistryObject().get(), info.CentrifugeData.getMainInputCount())),
                NonNullList.from(
                        Pair.of(ItemStack.EMPTY, 0.0f),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getMainOutput()), info.CentrifugeData.getMainOutputCount()), info.CentrifugeData.getMainOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getSecondaryOutput()), info.CentrifugeData.getSecondaryOutputCount()), info.CentrifugeData.getSecondaryOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getBottleOutput()), info.CentrifugeData.getBottleOutputCount()), info.CentrifugeData.getBottleOutputWeight())
                ),
                Config.CENTRIFUGE_RECIPE_TIME.get() * 20,
                false
        );
    }

    private IRecipe<?> centrifugeHoneyCombBlockRecipe(String beeType, CustomBeeData info) {
        return new CentrifugeRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block_centrifuge"),
                Ingredient.fromStacks(new ItemStack(info.getCombBlockItemRegistryObject().get(), info.CentrifugeData.getMainInputCount())),
                NonNullList.from(
                        Pair.of(ItemStack.EMPTY, 0.0f),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getMainOutput()), info.CentrifugeData.getMainOutputCount() * 9), info.CentrifugeData.getMainOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getSecondaryOutput()), info.CentrifugeData.getSecondaryOutputCount() * 9), info.CentrifugeData.getSecondaryOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(info.CentrifugeData.getBottleOutput()), info.CentrifugeData.getBottleOutputCount() * 9), info.CentrifugeData.getBottleOutputWeight())
                ),
                Config.CENTRIFUGE_RECIPE_TIME.get() * 20,
                true
        );
    }

    private IRecipe<?> combBlockToCombRecipe(String beeType, CustomBeeData info) {
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_block_to_honeycomb"),
                "",
                new ItemStack(info.getCombRegistryObject().get(), 9),
                NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(info.getCombBlockItemRegistryObject().get())))
        );
    }

    public static RecipeManager getRecipeManager() {
        if (!recipeManager.recipes.getClass().equals(HashMap.class)) {
            recipeManager.recipes = new HashMap<>(recipeManager.recipes);
            recipeManager.recipes.replaceAll((t, v) -> new HashMap<>(recipeManager.recipes.get(t)));
        }

        return recipeManager;
    }
}
