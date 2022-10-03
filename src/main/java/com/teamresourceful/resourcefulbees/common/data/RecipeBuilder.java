package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.HoneycombItem;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.RecipeManagerAccessor;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
                    .forEach(this::addRecipe);
        }

        BeeRegistry.getRegistry().getFamilyTree().values().forEach(c -> c.forEach(f -> addRecipe(makeBreedingRecipe(c))));
    }

    public void addRecipe(Recipe<?> recipe) {
        ((RecipeManagerAccessor)getRecipeManager()).getRecipes().computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(recipe.getId(), recipe);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        setRecipeManager(event.getServerResources().getRecipeManager());
        //REQUIRED Check if this can be put in another place
        //its here so it can have the items in the data before the
        //recipes are generated.
        //TODO check if this is proper. BUILTIN.get()
        BeeRegistry.getRegistry().regenerateCustomBeeData(RegistryAccess.BUILTIN.get());
        LOGGER.info("Adding Reload Listener: 'resourcefulbees recipe manager'");
    }

    private Recipe<?> makeBreedingRecipe(WeightedCollection<BeeFamily> families) {
        BeeFamily family = families.get(0);
        ResourceLocation id = new ResourceLocation(ResourcefulBees.MOD_ID, family.getParent1() + "_" + family.getParent2() + "_" + family.child());
        ResourceLocation parent1Id = family.getParent1Data().registryID();
        BeeJarIngredient beeJarParent1 = new BeeJarIngredient(parent1Id, family.getParent1Data().renderData().colorData().jarColor().getValue());
        BreedData parent1BreedData = family.getParent1Data().breedData();
        var parent1FeedItems = Ingredient.of(parent1BreedData.feedItems().stream().map(ItemStack::new));
        BreederRecipe.BreederPair parent1 = new BreederRecipe.BreederPair(beeJarParent1, Optional.of(parent1Id.toString()), parent1FeedItems, parent1BreedData.feedReturnItem());
        ResourceLocation parent2Id = family.getParent2Data().registryID();
        BreedData parent2BreedData = family.getParent2Data().breedData();
        BeeJarIngredient beeJarParent2 = new BeeJarIngredient(parent2Id, family.getParent2Data().renderData().colorData().jarColor().getValue());
        var parent2FeedItems = Ingredient.of(parent2BreedData.feedItems().stream().map(ItemStack::new));
        BreederRecipe.BreederPair parent2 = new BreederRecipe.BreederPair(beeJarParent2, Optional.of(parent2Id.toString()),  parent2FeedItems, parent2BreedData.feedReturnItem());
        return new BreederRecipe(id, parent1, parent2, Optional.of(Ingredient.of(ModItems.BEE_JAR.get())), families.stream().map(this::makeOutput).collect(WeightedCollection.getCollector(BreederRecipe.BreederOutput::weight)), 2400);
    }

    private BreederRecipe.BreederOutput makeOutput(BeeFamily family) {
        ItemStack childBeeJar = BeeJarIngredient.getBeeJar(family.getChildData().registryID(), family.getChildData().renderData().colorData().jarColor().getValue());
        return new BreederRecipe.BreederOutput(childBeeJar, Optional.of(family.getChildData().registryID().toString()), family.weight(), family.chance());
    }

    private Recipe<?> makeHoneycombRecipe(HoneycombItem comb) {
        Ingredient honeycombItem = Ingredient.of(comb);
        return new ShapedRecipe(
                Objects.requireNonNull(Registry.ITEM.getKey(comb.getStorageBlockItem())),
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
        Ingredient honeyBottleItem = Ingredient.of(info.bottleData().honeyBottle().get());
        return new ShapedRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_honey_block"),
                "",
                2,
                2,
                NonNullList.of(Ingredient.EMPTY,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem
                ),
                new ItemStack(info.blockData().blockItem().get())
        );
    }

    private Recipe<?> makeBottleToBucketRecipe(HoneyData info) {
        Ingredient honeyBottleItem = Ingredient.of(info.bottleData().honeyBottle().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_bottle_to_bucket"),
                "",
                new ItemStack(info.fluidData().fluidBucket().get()),
                NonNullList.of(Ingredient.EMPTY,
                        bucketItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem
                )
        );
    }

    private Recipe<?> makeBucketToBottleRecipe(HoneyData info) {
        Ingredient honeyBucketItem = Ingredient.of(info.fluidData().fluidBucket().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_bucket_to_bottle"),
                "",
                new ItemStack(info.bottleData().honeyBottle().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        bottleItem, bottleItem,
                        bottleItem, bottleItem,
                        honeyBucketItem
                )
        );
    }

    private Recipe<?> makeBlockToBucketRecipe(HoneyData info) {
        Ingredient honeyBlockItem = Ingredient.of(info.blockData().blockItem().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_block_to_bucket"),
                "",
                new ItemStack(info.fluidData().fluidBucket().get()),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bucketItem
                )
        );
    }

    private Recipe<?> makeFluidToBlockRecipe(HoneyData info) {
        return new SolidificationRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_fluid_to_block"),
                new FluidStack(info.fluidData().stillFluid().get(), 1000),
                new ItemStack(info.blockData().blockItem().get())
        );
    }

    private Recipe<?> makeBucketToBlockRecipe(HoneyData info) {
        Ingredient honeyBucketItem = Ingredient.of(info.fluidData().fluidBucket().get());
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_bucket_to_block"),
                "",
                new ItemStack(info.blockData().blockItem().get()),
                NonNullList.of(Ingredient.EMPTY, honeyBucketItem)
        );
    }

    private Recipe<?> makeHoneyBottleRecipe(HoneyData info) {
        Ingredient honeyBlockItem = Ingredient.of(info.blockData().blockItem().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ResourcefulBees.MOD_ID, info.name() + "_honey_bottle"),
                "",
                new ItemStack(info.bottleData().honeyBottle().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bottleItem,
                        bottleItem, bottleItem,
                        bottleItem
                )
        );
    }

    private Recipe<?> makeCombBlockToCombRecipe(HoneycombItem comb) {
        return new ShapelessRecipe(
                Objects.requireNonNull(Registry.ITEM.getKey(comb)),
                "",
                new ItemStack(comb, 9),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(comb.getStorageBlockItem()))
        );
    }

    public static RecipeManager getRecipeManager() {
        RecipeManagerAccessor recipeManagerInvoker = (RecipeManagerAccessor)recipeManager;
        if (!recipeManagerInvoker.getRecipes().getClass().equals(HashMap.class)) {
            recipeManagerInvoker.setRecipes(new HashMap<>(recipeManagerInvoker.getRecipes()));
            recipeManagerInvoker.getRecipes().replaceAll((t, v) -> new HashMap<>(recipeManagerInvoker.getRecipes().get(t)));
        }

        return recipeManager;
    }
}
