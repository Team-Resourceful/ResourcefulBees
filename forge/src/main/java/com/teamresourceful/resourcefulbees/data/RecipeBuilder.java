package com.teamresourceful.resourcefulbees.data;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.CentrifugeData;
import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.lib.HoneycombTypes;
import com.teamresourceful.resourcefulbees.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.HoneyRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class RecipeBuilder implements ResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private static void setRecipeManager(RecipeManager recipeManager) {
        RecipeBuilder.recipeManager = recipeManager;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        LOGGER.info("Loading comb recipes for {} bees...", BEE_REGISTRY.getBees().size());

        BEE_REGISTRY.getBees().forEach((s, beeData) -> {
            CentrifugeData centrifugeData = beeData.getCentrifugeData();
            HoneycombData honeycombData = beeData.getHoneycombData();

            if (centrifugeData.hasCentrifugeOutput() && honeycombData.getHoneycombType().equals(HoneycombTypes.DEFAULT)) {
                Recipe<?> honeycombCentrifuge = this.centrifugeRecipe(s, centrifugeData, honeycombData, 1);
                Recipe<?> honeycombBlockCentrifuge = this.centrifugeRecipe(s, centrifugeData, honeycombData, 9);
                getRecipeManager().recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
            }
            if (Config.HONEYCOMB_BLOCK_RECIPES.get() && honeycombData.getHoneycombType().equals(HoneycombTypes.DEFAULT)) {
                Recipe<?> honeycombBlock = this.makeHoneycombRecipe(s, honeycombData);
                Recipe<?> honeycomb = this.combBlockToCombRecipe(s, honeycombData);
                getRecipeManager().recipes.computeIfAbsent(honeycombBlock.getType(), t -> new HashMap<>()).put(honeycombBlock.getId(), honeycombBlock);
                getRecipeManager().recipes.computeIfAbsent(honeycomb.getType(), t -> new HashMap<>()).put(honeycomb.getId(), honeycomb);
            }
        });

        if (Config.HONEY_BLOCK_RECIPES.get() && Config.HONEY_GENERATE_BLOCKS.get()) {
            HoneyRegistry.getRegistry().getHoneyBottles().forEach((s, honeyData) -> {
                if (honeyData.doGenerateHoneyBlock() && honeyData.doHoneyBlockRecipe()) {
                    Recipe<?> honeyBlock = this.makeHoneyBlockRecipe(honeyData);
                    Recipe<?> honeyBottle = this.makeHoneyBottleRecipe(honeyData);
                    Recipe<?> bottleToBucket = this.makeBottleToBucketRecipe(honeyData);
                    Recipe<?> bucketToBottle = this.makeBucketToBottleRecipe(honeyData);
                    Recipe<?> blockToBucket = this.makeBlockToBucketRecipe(honeyData);
                    Recipe<?> bucketToBlock = this.makeBucketToBlockRecipe(honeyData);
                    getBottleRecipes(honeyBlock, honeyBottle, bottleToBucket);
                    getBottleRecipes(bucketToBottle, blockToBucket, bucketToBlock);
                }
            });
        }
    }

    //TODO simplify further
    public void getBottleRecipes(Recipe<?> recipe, Recipe<?> recipe1, Recipe<?> recipe2) {
        getRecipeManager().recipes.computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(recipe.getId(), recipe);
        getRecipeManager().recipes.computeIfAbsent(recipe1.getType(), t -> new HashMap<>()).put(recipe1.getId(), recipe1);
        getRecipeManager().recipes.computeIfAbsent(recipe2.getType(), t -> new HashMap<>()).put(recipe2.getId(), recipe2);
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        setRecipeManager(event.getDataPackRegistries().getRecipeManager());
        LOGGER.info("Adding Reload Listener: 'resourcefulbees recipe manager'");
    }

    private Recipe<?> makeHoneycombRecipe(String beeType, HoneycombData info) {
        Ingredient honeycombItem = Ingredient.of(info.getHoneycomb());
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
                new ItemStack(info.getHoneycombBlock())
        );
    }

    private Recipe<?> makeHoneyBlockRecipe(HoneyBottleData info) {
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

    private Recipe<?> makeBottleToBucketRecipe(HoneyBottleData info) {
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

    private Recipe<?> makeBucketToBottleRecipe(HoneyBottleData info) {
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

    private Recipe<?> makeBlockToBucketRecipe(HoneyBottleData info) {
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

    private Recipe<?> makeBucketToBlockRecipe(HoneyBottleData info) {
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

    private Recipe<?> makeHoneyBottleRecipe(HoneyBottleData info) {
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


    private Recipe<?> centrifugeRecipe(String beeType, CentrifugeData centrifugeData, HoneycombData honeycombData, int multiplier) {
        boolean isBlockRecipe = multiplier != 1;

        ResourceLocation recipeLoc = isBlockRecipe ? new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_block_centrifuge")
                : new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_honeycomb_centrifuge");

        Ingredient ingredient = isBlockRecipe ? Ingredient.of(new ItemStack(honeycombData.getHoneycombBlock(), centrifugeData.getInputCount())) :
                Ingredient.of(new ItemStack(honeycombData.getHoneycomb(), centrifugeData.getInputCount()));

        return new CentrifugeRecipe(
                recipeLoc, ingredient,
                centrifugeData.getItemOutputs(),
                centrifugeData.getFluidOutputs(),
                centrifugeData.getRecipeTime(),
                (centrifugeData.getRecipeTime() - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get()) * (isBlockRecipe ? 3 : 1),
                isBlockRecipe
        );
    }

    private Recipe<?> combBlockToCombRecipe(String beeType, HoneycombData info) {
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_block_to_honeycomb"),
                "",
                new ItemStack(info.getHoneycomb(), 9),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(new ItemStack(info.getHoneycombBlock())))
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
