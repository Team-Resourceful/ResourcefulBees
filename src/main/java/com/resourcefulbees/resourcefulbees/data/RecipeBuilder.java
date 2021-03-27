package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.beedata.CentrifugeData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
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

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class RecipeBuilder implements IResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private static void setRecipeManager(RecipeManager recipeManager) {
        RecipeBuilder.recipeManager = recipeManager;
    }

    @Override
    public void onResourceManagerReload(@NotNull IResourceManager resourceManager) {
        BEE_REGISTRY.getBees().forEach(((s, customBeeData) -> {
            if (customBeeData.hasHoneycomb()) {
                CentrifugeData centrifugeData = customBeeData.getCentrifugeData();
                centrifugeData.init();

                if (centrifugeData.hasCentrifugeOutput() && Config.CENTRIFUGE_RECIPES.get()) {
                    SecondPhaseValidator.validateCentrifugeOutputs(customBeeData);

                    if (centrifugeData.hasCentrifugeOutput()) {
                        IRecipe<?> honeycombCentrifuge = this.centrifugeRecipe(s, customBeeData, 1);
                        IRecipe<?> honeycombBlockCentrifuge = this.centrifugeRecipe(s, customBeeData, 9);
                        IRecipe<?> honeycombCentrifugeNoBottle = this.centrifugeRecipeNoBottle(s, customBeeData, 1);
                        IRecipe<?> honeycombBlockCentrifugeNoBottle = this.centrifugeRecipeNoBottle(s, customBeeData, 9);
                        getRecipeManager().recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                        getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
                        if (honeycombBlockCentrifugeNoBottle != null && honeycombCentrifugeNoBottle != null) {
                            getRecipeManager().recipes.computeIfAbsent(honeycombCentrifugeNoBottle.getType(), t -> new HashMap<>()).put(honeycombCentrifugeNoBottle.getId(), honeycombCentrifugeNoBottle);
                            getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifugeNoBottle.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifugeNoBottle.getId(), honeycombBlockCentrifugeNoBottle);
                        }
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
        if (Config.HONEY_BLOCK_RECIPES.get() && Config.HONEY_GENERATE_BLOCKS.get()) {
            BEE_REGISTRY.getHoneyBottles().forEach((s, honeyData) -> {
                SecondPhaseValidator.validateHoneyEffects(honeyData);
                if (honeyData.doGenerateHoneyBlock() && honeyData.doHoneyBlockRecipe()) {
                    IRecipe<?> honeyBlock = this.makeHoneyBlockRecipe(honeyData);
                    IRecipe<?> honeyBottle = this.makeHoneyBottleRecipe(honeyData);
                    IRecipe<?> bottleToBucket = this.makeBottleToBucketRecipe(honeyData);
                    IRecipe<?> bucketToBottle = this.makeBucketToBottleRecipe(honeyData);
                    IRecipe<?> blockToBucket = this.makeBlockToBucketRecipe(honeyData);
                    IRecipe<?> bucketToBlock = this.makeBucketToBlockRecipe(honeyData);
                    getBottleRecipes(honeyBlock, honeyBottle, bottleToBucket);
                    getBottleRecipes(bucketToBottle, blockToBucket, bucketToBlock);
                }
            });
        }
    }

    //TODO simplify further
    public void getBottleRecipes(IRecipe<?> recipe, IRecipe<?> recipe1, IRecipe<?> recipe2) {
        getRecipeManager().recipes.computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(recipe.getId(), recipe);
        getRecipeManager().recipes.computeIfAbsent(recipe1.getType(), t -> new HashMap<>()).put(recipe1.getId(), recipe1);
        getRecipeManager().recipes.computeIfAbsent(recipe2.getType(), t -> new HashMap<>()).put(recipe2.getId(), recipe2);
    }


    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        setRecipeManager(event.getDataPackRegistries().getRecipeManager());
    }

    private IRecipe<?> makeHoneycombRecipe(String beeType, CustomBeeData info) {
        Ingredient honeycombItem = Ingredient.of(info.getCombRegistryObject().get());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block"),
                "",
                3,
                3,
                NonNullList.of(Ingredient.EMPTY,
                        honeycombItem, honeycombItem, honeycombItem,
                        honeycombItem, honeycombItem, honeycombItem,
                        honeycombItem, honeycombItem, honeycombItem
                ),
                new ItemStack(info.getCombBlockItemRegistryObject().get())
        );
    }

    private IRecipe<?> makeHoneyBlockRecipe(HoneyBottleData info) {
        Ingredient honeyBottleItem = Ingredient.of(info.getHoneyBottleRegistryObject().get());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_honey_block"),
                "",
                2,
                2,
                NonNullList.of(Ingredient.EMPTY,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem
                ),
                new ItemStack(info.getHoneyBlockItemRegistryObject().get())
        );
    }

    private IRecipe<?> makeBottleToBucketRecipe(HoneyBottleData info) {
        Ingredient honeyBottleItem = Ingredient.of(info.getHoneyBottleRegistryObject().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bottle_to_bucket"),
                "",
                new ItemStack(info.getHoneyBucketItemRegistryObject().get()),
                NonNullList.of(Ingredient.EMPTY,
                        bucketItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem
                )
        );
    }

    private IRecipe<?> makeBucketToBottleRecipe(HoneyBottleData info) {
        Ingredient honeyBucketItem = Ingredient.of(info.getHoneyBucketItemRegistryObject().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bucket_to_bottle"),
                "",
                new ItemStack(info.getHoneyBottleRegistryObject().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        bottleItem, bottleItem,
                        bottleItem, bottleItem,
                        honeyBucketItem
                )
        );
    }

    private IRecipe<?> makeBlockToBucketRecipe(HoneyBottleData info) {
        Ingredient honeyBlockItem = Ingredient.of(info.getHoneyBlockItemRegistryObject().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_block_to_bucket"),
                "",
                new ItemStack(info.getHoneyBucketItemRegistryObject().get()),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bucketItem
                )
        );
    }

    private IRecipe<?> makeBucketToBlockRecipe(HoneyBottleData info) {
        Ingredient honeyBucketItem = Ingredient.of(info.getHoneyBucketItemRegistryObject().get());
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bucket_to_block"),
                "",
                new ItemStack(info.getHoneyBlockItemRegistryObject().get()),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBucketItem
                )
        );
    }

    private IRecipe<?> makeHoneyBottleRecipe(HoneyBottleData info) {
        Ingredient honeyBlockItem = Ingredient.of(info.getHoneyBlockItemRegistryObject().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_honey_bottle"),
                "",
                new ItemStack(info.getHoneyBottleRegistryObject().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bottleItem,
                        bottleItem, bottleItem,
                        bottleItem
                )
        );
    }

    private IRecipe<?> centrifugeRecipeNoBottle(String beeType, CustomBeeData info, int multiplier) {
        CentrifugeData data = info.getCentrifugeData();
        boolean isBlockRecipe = multiplier > 1;

        ItemStack mainOutput = data.isMainIsFluidOutput() ? ItemStack.EMPTY : new ItemStack(BeeInfoUtils.getItem(data.getMainOutput()), data.getMainOutputCount() * multiplier);
        ItemStack secondaryOutput = new ItemStack(BeeInfoUtils.getItem(data.getSecondaryOutput()), data.getSecondaryOutputCount() * multiplier);
        ItemStack bottleOutput = new ItemStack(BeeInfoUtils.getItem(data.getBottleOutput()), data.getBottleOutputCount() * multiplier);

        FluidStack fluidOutput = data.hasFluidOutput() ? new FluidStack(BeeInfoUtils.getFluid(data.getFluidOutput()), data.getFluidOutputCount() * multiplier) : FluidStack.EMPTY;

        mainOutput.setTag(data.getMainNBT());
        secondaryOutput.setTag(data.getSecondaryNBT());
        bottleOutput.setTag(data.getBottleNBT());

        FluidStack bottleFluid = new FluidStack(BeeInfoUtils.getFluidFromBottle(bottleOutput), ModConstants.HONEY_PER_BOTTLE * multiplier);

        ResourceLocation recipeLoc = isBlockRecipe ? new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block_centrifuge_no_bottle")
                : new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_centrifuge_no_bottle");

        if (bottleFluid.isEmpty()) return null;

        return new CentrifugeRecipe(
                recipeLoc,
                isBlockRecipe ? Ingredient.of(new ItemStack(info.getCombBlockRegistryObject().get(), data.getMainInputCount())) :
                        Ingredient.of(new ItemStack(info.getCombRegistryObject().get(), data.getMainInputCount())),
                NonNullList.of(
                        Pair.of(ItemStack.EMPTY, 0f),
                        Pair.of(mainOutput, data.getMainOutputWeight()),
                        Pair.of(secondaryOutput, data.getSecondaryOutputWeight())
                ),
                NonNullList.of(
                        Pair.of(FluidStack.EMPTY, 0f),
                        Pair.of(fluidOutput, data.getFluidOutputWeight()),
                        Pair.of(bottleFluid, data.getBottleOutputWeight())
                ),
                data.getRecipeTime(),
                data.getRecipeTime() - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get(),
                false,
                data.hasFluidOutput(), true
        );
    }

    private IRecipe<?> centrifugeRecipe(String beeType, CustomBeeData info, int multiplier) {
        CentrifugeData data = info.getCentrifugeData();
        boolean isBlockRecipe = multiplier > 1;

        ItemStack mainOutput = data.isMainIsFluidOutput() ? ItemStack.EMPTY : new ItemStack(BeeInfoUtils.getItem(data.getMainOutput()), data.getMainOutputCount() * multiplier);
        ItemStack secondaryOutput = new ItemStack(BeeInfoUtils.getItem(data.getSecondaryOutput()), data.getSecondaryOutputCount() * multiplier);
        ItemStack bottleOutput = new ItemStack(BeeInfoUtils.getItem(data.getBottleOutput()), data.getBottleOutputCount() * multiplier);

        FluidStack fluidOutput = data.hasFluidOutput() ? new FluidStack(BeeInfoUtils.getFluid(data.getFluidOutput()), data.getFluidOutputCount() * multiplier) : FluidStack.EMPTY;

        mainOutput.setTag(data.getMainNBT());
        secondaryOutput.setTag(data.getSecondaryNBT());
        bottleOutput.setTag(data.getBottleNBT());

        ResourceLocation recipeLoc = isBlockRecipe ? new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block_centrifuge")
                : new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_centrifuge");

        return new CentrifugeRecipe(
                recipeLoc,
                isBlockRecipe ? Ingredient.of(new ItemStack(info.getCombBlockRegistryObject().get(), data.getMainInputCount())) :
                        Ingredient.of(new ItemStack(info.getCombRegistryObject().get(), data.getMainInputCount())),
                NonNullList.of(
                        Pair.of(ItemStack.EMPTY, 0f),
                        Pair.of(mainOutput, data.getMainOutputWeight()),
                        Pair.of(secondaryOutput, data.getSecondaryOutputWeight()),
                        Pair.of(bottleOutput, data.getBottleOutputWeight())
                ),
                NonNullList.of(
                        Pair.of(FluidStack.EMPTY, 0f),
                        Pair.of(fluidOutput, data.getFluidOutputWeight())),
                data.getRecipeTime(),
                data.getRecipeTime() - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get(),
                false,
                data.hasFluidOutput(), false
        );
    }

    private IRecipe<?> combBlockToCombRecipe(String beeType, CustomBeeData info) {
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_block_to_honeycomb"),
                "",
                new ItemStack(info.getCombRegistryObject().get(), 9),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(new ItemStack(info.getCombBlockItemRegistryObject().get())))
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
