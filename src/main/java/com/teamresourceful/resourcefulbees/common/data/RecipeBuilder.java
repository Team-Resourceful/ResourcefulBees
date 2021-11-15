package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
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
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

@SuppressWarnings("deprecation")
public class RecipeBuilder implements IResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static void setRecipeManager(RecipeManager recipeManager) {
        RecipeBuilder.recipeManager = recipeManager;
    }

    @Override
    public void onResourceManagerReload(@NotNull IResourceManager resourceManager) {
        if (Boolean.TRUE.equals(CommonConfig.HONEYCOMB_BLOCK_RECIPES.get())) {
            LOGGER.info("Generating comb recipes for {} honeycombs...", ModItems.HONEYCOMB_ITEMS.getEntries().size());
            ModItems.HONEYCOMB_ITEMS.getEntries().stream()
                    .filter(RegistryObject::isPresent)
                    .map(RegistryObject::get)
                    .filter(HoneycombItem.class::isInstance)
                    .map(HoneycombItem.class::cast)
                    .filter(HoneycombItem::hasStorageBlockItem)
                    .flatMap(comb -> Stream.of(makeHoneycombRecipe(comb), makeCombBlockToCombRecipe(comb)))
                    .forEach(this::addRecipe);
        }

        if (CommonConfig.HONEY_BLOCK_RECIPES.get() && CommonConfig.HONEY_GENERATE_BLOCKS.get()) {
            HoneyRegistry.getRegistry().getHoneyBottles().values().stream()
                    .flatMap(data ->
                        Stream.of(
                            makeHoneyBlockRecipe(data),
                            makeHoneyBottleRecipe(data),
                            makeBottleToBucketRecipe(data),
                            makeBucketToBottleRecipe(data),
                            makeBlockToBucketRecipe(data),
                            makeBucketToBlockRecipe(data)
                        )
                    )
                    .filter(Objects::nonNull)
                    .forEach(this::addRecipe);
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

    private IRecipe<?> makeHoneycombRecipe(HoneycombItem comb) {
        Ingredient honeycombItem = Ingredient.of(comb);
        return new ShapedRecipe(
                Objects.requireNonNull(comb.getStorageBlockItem().getRegistryName()),
                "",
                3,
                3,
                NonNullList.of(Ingredient.EMPTY,
                        honeycombItem, honeycombItem, honeycombItem,
                        honeycombItem, honeycombItem, honeycombItem,
                        honeycombItem, honeycombItem, honeycombItem
                ),
                new ItemStack(comb.getStorageBlockItem())
        );
    }

    private IRecipe<?> makeHoneyBlockRecipe(HoneyData info) {
        if (info.getBlockData().getBlockItem() == null) return null;
        Ingredient honeyBottleItem = Ingredient.of(info.getBottleData().getHoneyBottle().get());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_honey_block"),
                "",
                2,
                2,
                NonNullList.of(Ingredient.EMPTY,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem
                ),
                new ItemStack(info.getBlockData().getBlockItem().get())
        );
    }

    private IRecipe<?> makeBottleToBucketRecipe(HoneyData info) {
        if (info.getFluidData().getFluidBucket() == null) return null;
        Ingredient honeyBottleItem = Ingredient.of(info.getBottleData().getHoneyBottle().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bottle_to_bucket"),
                "",
                new ItemStack(info.getFluidData().getFluidBucket().get()),
                NonNullList.of(Ingredient.EMPTY,
                        bucketItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem
                )
        );
    }

    private IRecipe<?> makeBucketToBottleRecipe(HoneyData info) {
        if (info.getFluidData().getFluidBucket() == null) return null;
        Ingredient honeyBucketItem = Ingredient.of(info.getFluidData().getFluidBucket().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bucket_to_bottle"),
                "",
                new ItemStack(info.getBottleData().getHoneyBottle().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        bottleItem, bottleItem,
                        bottleItem, bottleItem,
                        honeyBucketItem
                )
        );
    }

    private IRecipe<?> makeBlockToBucketRecipe(HoneyData info) {
        if (info.getFluidData().getFluidBucket() == null || info.getBlockData().getBlockItem() == null) return null;
        Ingredient honeyBlockItem = Ingredient.of(info.getBlockData().getBlockItem().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_block_to_bucket"),
                "",
                new ItemStack(info.getFluidData().getFluidBucket().get()),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bucketItem
                )
        );
    }

    private IRecipe<?> makeBucketToBlockRecipe(HoneyData info) {
        if (info.getFluidData().getFluidBucket() == null || info.getBlockData().getBlockItem() == null) return null;
        Ingredient honeyBucketItem = Ingredient.of(info.getFluidData().getFluidBucket().get());
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_bucket_to_block"),
                "",
                new ItemStack(info.getBlockData().getBlockItem().get()),
                NonNullList.of(Ingredient.EMPTY, honeyBucketItem)
        );
    }

    private IRecipe<?> makeHoneyBottleRecipe(HoneyData info) {
        if (info.getBlockData().getBlockItem() == null) return null;
        Ingredient honeyBlockItem = Ingredient.of(info.getBlockData().getBlockItem().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.getName() + "_honey_bottle"),
                "",
                new ItemStack(info.getBottleData().getHoneyBottle().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bottleItem,
                        bottleItem, bottleItem,
                        bottleItem
                )
        );
    }

    private IRecipe<?> makeCombBlockToCombRecipe(HoneycombItem comb) {
        return new ShapelessRecipe(
                Objects.requireNonNull(comb.getRegistryName()),
                "",
                new ItemStack(comb, 9),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(comb.getStorageBlockItem()))
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
