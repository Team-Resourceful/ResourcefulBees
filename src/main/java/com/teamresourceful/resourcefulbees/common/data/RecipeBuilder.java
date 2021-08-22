package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.lib.enums.HoneycombType;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.HoneyRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class RecipeBuilder implements IResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private static void setRecipeManager(RecipeManager recipeManager) {
        RecipeBuilder.recipeManager = recipeManager;
    }

    @Override
    public void onResourceManagerReload(@NotNull IResourceManager resourceManager) {
        LOGGER.info("Generating comb recipes for {} bees...", BEE_REGISTRY.getBees().size());

        BEE_REGISTRY.getBees().forEach((s, beeData) -> {
            CentrifugeData centrifugeData = beeData.getCentrifugeData();
            HoneycombData honeycombData = beeData.getHoneycombData();

            if (centrifugeData.hasCentrifugeOutput() && honeycombData.getHoneycombType().equals(HoneycombType.DEFAULT)) {
                addRecipe(this.makeCentrifugeRecipe(s, centrifugeData, honeycombData, 1));
                addRecipe(this.makeCentrifugeRecipe(s, centrifugeData, honeycombData, 9));
            }
            if (Config.HONEYCOMB_BLOCK_RECIPES.get() && honeycombData.getHoneycombType().equals(HoneycombType.DEFAULT)) {
                addRecipe(this.makeHoneycombRecipe(s, honeycombData));
                addRecipe(this.makeCombBlockToCombRecipe(s, honeycombData));
            }
        });

        if (Config.HONEY_BLOCK_RECIPES.get() && Config.HONEY_GENERATE_BLOCKS.get()) {
            HoneyRegistry.getRegistry().getHoneyBottles().forEach((s, honeyData) -> {
                if (honeyData.doGenerateHoneyBlock() && honeyData.doHoneyBlockRecipe()) {
                    addRecipe(this.makeHoneyBlockRecipe(honeyData));
                    addRecipe(this.makeHoneyBottleRecipe(honeyData));
                    addRecipe(this.makeBottleToBucketRecipe(honeyData));
                    addRecipe(this.makeBucketToBottleRecipe(honeyData));
                    addRecipe(this.makeBlockToBucketRecipe(honeyData));
                    addRecipe(this.makeBucketToBlockRecipe(honeyData));
                }
            });
        }
    }

    public void addRecipe(IRecipe<?> recipe) {
        ((RecipeManagerAccessorInvoker)getRecipeManager()).getRecipes().computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(recipe.getId(), recipe);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        setRecipeManager(event.getDataPackRegistries().getRecipeManager());
        //REQUIRED Check if this can be put in another place
        //its here so it can have the items in the data before the
        //recipes are generated.
        BeeRegistry.getRegistry().regenerateCustomBeeData();
        LOGGER.info("Adding Reload Listener: 'resourcefulbees recipe manager'");
    }

    private IRecipe<?> makeHoneycombRecipe(String beeType, HoneycombData info) {
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


    private IRecipe<?> makeCentrifugeRecipe(String beeType, CentrifugeData centrifugeData, HoneycombData honeycombData, int multiplier) {
        boolean isBlockRecipe = multiplier != 1;
        ResourceLocation recipeLoc = new ResourceLocation(ResourcefulBees.MOD_ID, beeType + (isBlockRecipe? "_honeycomb_block" : "_honeycomb") + "_centrifuge");

        Ingredient ingredient = Ingredient.of(new ItemStack(isBlockRecipe ? honeycombData.getHoneycombBlock() : honeycombData.getHoneycomb(), centrifugeData.getInputCount()));

        return new CentrifugeRecipe(
                recipeLoc, ingredient,
                centrifugeData.getItemOutputs(),
                centrifugeData.getFluidOutputs(),
                centrifugeData.getRecipeTime(),
                (centrifugeData.getRecipeTime() - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get()) * (isBlockRecipe ? 3 : 1),
                isBlockRecipe
        );
    }

    private IRecipe<?> makeCombBlockToCombRecipe(String beeType, HoneycombData info) {
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, beeType + "_block_to_honeycomb"),
                "",
                new ItemStack(info.getHoneycomb(), 9),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(new ItemStack(info.getHoneycombBlock())))
        );
    }

    public static RecipeManager getRecipeManager() {
        RecipeManagerAccessorInvoker recipeManagerInvoker = (RecipeManagerAccessorInvoker)recipeManager;
        if (!recipeManagerInvoker.getRecipes().getClass().equals(HashMap.class)) {
            recipeManagerInvoker.setRecipes(new HashMap<>(recipeManagerInvoker.getRecipes()));
            recipeManagerInvoker.getRecipes().replaceAll((t, v) -> new HashMap<>(recipeManagerInvoker.getRecipes().get(t)));
        }

        return recipeManager;
    }
}
