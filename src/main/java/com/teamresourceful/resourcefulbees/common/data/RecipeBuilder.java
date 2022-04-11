package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class RecipeBuilder implements ResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static void setRecipeManager(RecipeManager recipeManager) {
        RecipeBuilder.recipeManager = recipeManager;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {

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
                            makeBucketToBlockRecipe(data),
                            makeFluidToBlockRecipe(data)
                        )
                    )
                    .filter(Objects::nonNull)
                    .forEach(this::addRecipe);
        }

        BeeRegistry.getRegistry().getFamilyTree().values().forEach(c -> c.forEach(f -> addRecipe(makeBreedingRecipe(c))));
    }

    public void addRecipe(Recipe<?> recipe) {
        ((RecipeManagerAccessorInvoker)getRecipeManager()).getRecipes().computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(recipe.getId(), recipe);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        setRecipeManager(event.getServerResources().getRecipeManager());
        //REQUIRED Check if this can be put in another place
        //its here so it can have the items in the data before the
        //recipes are generated.
        BeeRegistry.getRegistry().regenerateCustomBeeData();
        HoneyRegistry.getRegistry().regenerateHoneyData();
        LOGGER.info("Adding Reload Listener: 'resourcefulbees recipe manager'");
    }

    private Recipe<?> makeBreedingRecipe(RandomCollection<BeeFamily> families) {
        BeeFamily family = families.get(0);
        ResourceLocation id = new ResourceLocation(ResourcefulBees.MOD_ID, family.getParent1() + "_" + family.getParent2() + "_" + family.getChild());
        ResourceLocation parent1Id = family.getParent1Data().getRegistryID();
        BeeJarIngredient beeJarParent1 = new BeeJarIngredient(parent1Id, family.getParent1Data().getRenderData().colorData().jarColor().getValue());
        BreedData parent1BreedData = family.getParent1Data().getBreedData();
        var parent1FeedItems = Ingredient.of(parent1BreedData.getFeedItems().stream().map(ItemStack::new));
        BreederRecipe.BreederPair parent1 = new BreederRecipe.BreederPair(beeJarParent1, Optional.of(parent1Id.toString()), parent1FeedItems, parent1BreedData.getFeedReturnItem());
        ResourceLocation parent2Id = family.getParent2Data().getRegistryID();
        BreedData parent2BreedData = family.getParent2Data().getBreedData();
        BeeJarIngredient beeJarParent2 = new BeeJarIngredient(parent2Id, family.getParent2Data().getRenderData().colorData().jarColor().getValue());
        var parent2FeedItems = Ingredient.of(parent2BreedData.getFeedItems().stream().map(ItemStack::new));
        BreederRecipe.BreederPair parent2 = new BreederRecipe.BreederPair(beeJarParent2, Optional.of(parent2Id.toString()),  parent2FeedItems, parent2BreedData.getFeedReturnItem());
        return new BreederRecipe(id, parent1, parent2, Optional.of(Ingredient.of(ModItems.BEE_JAR.get())), families.stream().map(this::makeOutput).collect(RandomCollection.getCollector(BreederRecipe.BreederOutput::weight)), 2400);
    }

    private BreederRecipe.BreederOutput makeOutput(BeeFamily family) {
        ItemStack childBeeJar = BeeJarIngredient.getBeeJar(family.getChildData().getRegistryID(), family.getChildData().getRenderData().colorData().jarColor().getValue());
        return new BreederRecipe.BreederOutput(childBeeJar, Optional.of(family.getChildData().getRegistryID().toString()), family.getWeight(), family.getChance());
    }

    private Recipe<?> makeHoneycombRecipe(HoneycombItem comb) {
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

    private Recipe<?> makeHoneyBlockRecipe(HoneyData info) {
        if (info.blockData().blockItem() == null) return null;
        Ingredient honeyBottleItem = Ingredient.of(info.bottleData().honeyBottle());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_honey_block"),
                "",
                2,
                2,
                NonNullList.of(Ingredient.EMPTY,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem
                ),
                new ItemStack(info.blockData().blockItem())
        );
    }

    private Recipe<?> makeBottleToBucketRecipe(HoneyData info) {
        if (info.fluidData().fluidBucket() == null) return null;
        Ingredient honeyBottleItem = Ingredient.of(info.bottleData().honeyBottle());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_bottle_to_bucket"),
                "",
                new ItemStack(info.fluidData().fluidBucket()),
                NonNullList.of(Ingredient.EMPTY,
                        bucketItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem
                )
        );
    }

    private Recipe<?> makeBucketToBottleRecipe(HoneyData info) {
        if (info.fluidData().fluidBucket() == null) return null;
        Ingredient honeyBucketItem = Ingredient.of(info.fluidData().fluidBucket());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_bucket_to_bottle"),
                "",
                new ItemStack(info.bottleData().honeyBottle(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        bottleItem, bottleItem,
                        bottleItem, bottleItem,
                        honeyBucketItem
                )
        );
    }

    private Recipe<?> makeBlockToBucketRecipe(HoneyData info) {
        if (info.fluidData().fluidBucket() == null || info.blockData().blockItem() == null) return null;
        Ingredient honeyBlockItem = Ingredient.of(info.blockData().blockItem());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_block_to_bucket"),
                "",
                new ItemStack(info.fluidData().fluidBucket()),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bucketItem
                )
        );
    }

    private Recipe<?> makeFluidToBlockRecipe(HoneyData info) {
        if (info.fluidData().stillFluid() == null || info.blockData().blockItem() == null) return null;
        return new SolidificationRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_fluid_to_block"),
                new FluidStack(info.fluidData().stillFluid(), 1000),
                new ItemStack(info.blockData().blockItem())
        );
    }

    private Recipe<?> makeBucketToBlockRecipe(HoneyData info) {
        if (info.fluidData().fluidBucket() == null || info.blockData().blockItem() == null) return null;
        Ingredient honeyBucketItem = Ingredient.of(info.fluidData().fluidBucket());
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_bucket_to_block"),
                "",
                new ItemStack(info.blockData().blockItem()),
                NonNullList.of(Ingredient.EMPTY, honeyBucketItem)
        );
    }

    private Recipe<?> makeHoneyBottleRecipe(HoneyData info) {
        if (info.blockData().blockItem() == null) return null;
        Ingredient honeyBlockItem = Ingredient.of(info.blockData().blockItem());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_honey_bottle"),
                "",
                new ItemStack(info.bottleData().honeyBottle(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bottleItem,
                        bottleItem, bottleItem,
                        bottleItem
                )
        );
    }

    private Recipe<?> makeCombBlockToCombRecipe(HoneycombItem comb) {
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
