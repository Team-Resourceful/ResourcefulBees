package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CentrifugeData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModPotions;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.validation.SecondPhaseValidator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class RecipeBuilder implements IResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        BEE_REGISTRY.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb()) {
                CentrifugeData centrifugeData = customBeeData.getCentrifugeData();

                if (centrifugeData.hasCentrifugeOutput() && Config.CENTRIFUGE_RECIPES.get()) {
                    SecondPhaseValidator.validateCentrifugeOutputs(customBeeData);

                    if (centrifugeData.hasCentrifugeOutput()) {
                        IRecipe<?> honeycombCentrifuge = this.centrifugeRecipe(s, customBeeData);
                        IRecipe<?> honeycombBlockCentrifuge = this.centrifugeHoneyCombBlockRecipe(s, customBeeData);
                        getRecipeManager().recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                        getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
                    }
                }

                if (Config.HONEYCOMB_BLOCK_RECIPES.get() && !customBeeData.hasCustomDrop()) {
                    IRecipe<?> honeycombBlock = this.makeHoneycombRecipe(s, customBeeData);
                    IRecipe<?> honeycomb = this.combBlockToCombRecipe(s, customBeeData);
                    getRecipeManager().recipes.computeIfAbsent(honeycombBlock.getType(), t -> new HashMap<>()).put(honeycombBlock.getId(), honeycombBlock);
                    getRecipeManager().recipes.computeIfAbsent(honeycomb.getType(), t -> new HashMap<>()).put(honeycomb.getId(), honeycomb);
                }
            }
        }));
        if (Config.HONEY_BLOCK_RECIPIES.get() && Config.HONEY_GENERATE_BLOCKS.get()) {
            BEE_REGISTRY.getHoneyBottles().forEach((s, honeyData) -> {
                if (honeyData.doGenerateHoneyBlock() && honeyData.doHoneyBlockRecipe()) {
                    IRecipe<?> honeyBlock = this.makeHoneyBlockRecipe(honeyData);
                    IRecipe<?> honeyBottle = this.makeHoneyBottleRecipe(honeyData);
                    IRecipe<?> bottleToBucket = this.makeBottleToBucketRecipe(honeyData);
                    IRecipe<?> bucketToBottle = this.makeBucketToBottleRecipe(honeyData);
                    IRecipe<?> blockToBucket = this.makeBlockToBucketRecipe(honeyData);
                    IRecipe<?> bucketToBlock = this.makeBucketToBlockRecipe(honeyData);
                    getRecipeManager().recipes.computeIfAbsent(honeyBlock.getType(), t -> new HashMap<>()).put(honeyBlock.getId(), honeyBlock);
                    getRecipeManager().recipes.computeIfAbsent(honeyBottle.getType(), t -> new HashMap<>()).put(honeyBottle.getId(), honeyBottle);
                    getRecipeManager().recipes.computeIfAbsent(bottleToBucket.getType(), t -> new HashMap<>()).put(bottleToBucket.getId(), bottleToBucket);
                    getRecipeManager().recipes.computeIfAbsent(bucketToBottle.getType(), t -> new HashMap<>()).put(bucketToBottle.getId(), bucketToBottle);
                    getRecipeManager().recipes.computeIfAbsent(blockToBucket.getType(), t -> new HashMap<>()).put(blockToBucket.getId(), blockToBucket);
                    getRecipeManager().recipes.computeIfAbsent(bucketToBlock.getType(), t -> new HashMap<>()).put(bucketToBlock.getId(), bucketToBlock);
                }
            });
        }
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

    private IRecipe<?> makeHoneyBlockRecipe(HoneyBottleData info) {
        Ingredient honeyBottleItem = Ingredient.fromItems(info.getHoneyBottleRegistryObject().get());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_honey_block"),
                "",
                2,
                2,
                NonNullList.from(Ingredient.EMPTY,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem
                ),
                new ItemStack(info.getHoneyBlockItemRegistryObject().get())
        );
    }

    private IRecipe<?> makeBottleToBucketRecipe(HoneyBottleData info) {
        Ingredient honeyBottleItem = Ingredient.fromItems(info.getHoneyBottleRegistryObject().get());
        Ingredient bucketItem = Ingredient.fromItems(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bottle_to_bucket"),
                "",
                new ItemStack(info.getHoneyBucketItemRegistryObject().get()),
                NonNullList.from(Ingredient.EMPTY,
                        bucketItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem
                )
        );
    }

    private IRecipe<?> makeBucketToBottleRecipe(HoneyBottleData info) {
        Ingredient honeyBucketItem = Ingredient.fromItems(info.getHoneyBucketItemRegistryObject().get());
        Ingredient bottleItem = Ingredient.fromItems(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bucket_to_bottle"),
                "",
                new ItemStack(info.getHoneyBottleRegistryObject().get(), 4),
                NonNullList.from(Ingredient.EMPTY,
                        bottleItem, bottleItem,
                        bottleItem, bottleItem,
                        honeyBucketItem
                )
        );
    }

    private IRecipe<?> makeBlockToBucketRecipe(HoneyBottleData info) {
        Ingredient honeyBlockItem = Ingredient.fromItems(info.getHoneyBlockItemRegistryObject().get());
        Ingredient bucketItem = Ingredient.fromItems(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_block_to_bucket"),
                "",
                new ItemStack(info.getHoneyBucketItemRegistryObject().get()),
                NonNullList.from(Ingredient.EMPTY,
                        honeyBlockItem, bucketItem
                )
        );
    }

    private IRecipe<?> makeBucketToBlockRecipe(HoneyBottleData info) {
        Ingredient honeyBucketItem = Ingredient.fromItems(info.getHoneyBucketItemRegistryObject().get());
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bucket_to_block"),
                "",
                new ItemStack(info.getHoneyBlockItemRegistryObject().get()),
                NonNullList.from(Ingredient.EMPTY,
                        honeyBucketItem
                )
        );
    }

    private IRecipe<?> makeHoneyBottleRecipe(HoneyBottleData info) {
        Ingredient honeyBlockItem = Ingredient.fromItems(info.getHoneyBlockItemRegistryObject().get());
        Ingredient bottleItem = Ingredient.fromItems(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_honey_bottle"),
                "",
                new ItemStack(info.getHoneyBottleRegistryObject().get(), 4),
                NonNullList.from(Ingredient.EMPTY,
                        honeyBlockItem, bottleItem,
                        bottleItem, bottleItem,
                        bottleItem
                )
        );
    }

    private IRecipe<?> centrifugeRecipe(String beeType, CustomBeeData info) {
        CentrifugeData data = info.getCentrifugeData();

        ItemStack mainOutput = data.hasFluidoutput() ? ItemStack.EMPTY : new ItemStack(BeeInfoUtils.getItem(data.getMainOutput()), data.getMainOutputCount());
        FluidStack fluidOutput = data.hasFluidoutput() ? new FluidStack(BeeInfoUtils.getFluid(data.getMainOutput()), data.getMainOutputCount()) : FluidStack.EMPTY;

        return new CentrifugeRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_centrifuge"),
                Ingredient.fromStacks(new ItemStack(info.getCombRegistryObject().get(), data.getMainInputCount())),
                NonNullList.from(
                        Pair.of(ItemStack.EMPTY, 0f),
                        Pair.of(mainOutput, data.getMainOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(data.getSecondaryOutput()), data.getSecondaryOutputCount()), data.getSecondaryOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(data.getBottleOutput()), data.getBottleOutputCount()), data.getBottleOutputWeight())
                ),
                NonNullList.from(
                        Pair.of(FluidStack.EMPTY, 0f),
                        Pair.of(fluidOutput, data.getMainOutputWeight())
                ),
                data.getRecipeTime(),
                data.getRecipeTime() - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get(),
                false,
                data.hasFluidoutput()
        );
    }

    private IRecipe<?> centrifugeHoneyCombBlockRecipe(String beeType, CustomBeeData info) {
        CentrifugeData data = info.getCentrifugeData();

        ItemStack mainOutput = data.hasFluidoutput() ? ItemStack.EMPTY : new ItemStack(BeeInfoUtils.getItem(data.getMainOutput()), data.getMainOutputCount() * 9);
        FluidStack fluidOutput = data.hasFluidoutput() ? new FluidStack(BeeInfoUtils.getFluid(data.getMainOutput()), data.getMainOutputCount() * 9) : FluidStack.EMPTY;


        return new CentrifugeRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block_centrifuge"),
                Ingredient.fromStacks(new ItemStack(info.getCombBlockItemRegistryObject().get(), data.getMainInputCount())),
                NonNullList.from(
                        Pair.of(ItemStack.EMPTY, 0.0f),
                        Pair.of(mainOutput, data.getMainOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(data.getSecondaryOutput()), data.getSecondaryOutputCount() * 9), data.getSecondaryOutputWeight()),
                        Pair.of(new ItemStack(BeeInfoUtils.getItem(data.getBottleOutput()), data.getBottleOutputCount() * 9), data.getBottleOutputWeight())
                ),
                NonNullList.from(
                        Pair.of(FluidStack.EMPTY, 0f),
                        Pair.of(fluidOutput, data.getMainOutputWeight())
                ),
                data.getRecipeTime(),
                (data.getRecipeTime() - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get()) * 3,
                true,
                data.hasFluidoutput()
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
