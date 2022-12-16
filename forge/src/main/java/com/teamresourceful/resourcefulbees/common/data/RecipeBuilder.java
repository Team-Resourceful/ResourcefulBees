package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.config.RecipeConfig;
import com.teamresourceful.resourcefulbees.common.item.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.items.CustomHoneycombItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.RecipeManagerAccessor;
import com.teamresourceful.resourcefulbees.common.recipe.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class RecipeBuilder implements ResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    private static void setRecipeManager(RecipeManager recipeManager) {
        RecipeBuilder.recipeManager = recipeManager;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {

        if (RecipeConfig.honeycombBlockRecipes) {
            ModConstants.LOGGER.info("Generating comb recipes for {} honeycombs...", ModItems.HONEYCOMB_ITEMS.getEntries().size());
            ModItems.HONEYCOMB_ITEMS.getEntries().stream()
                    .map(RegistryEntry::get)
                    .filter(CustomHoneycombItem.class::isInstance)
                    .map(CustomHoneycombItem.class::cast)
                    .filter(CustomHoneycombItem::hasStorageBlockItem)
                    .flatMap(comb -> Stream.of(makeHoneycombRecipe(comb), makeCombBlockToCombRecipe(comb)))
                    .forEach(this::addRecipe);
        }

        if (RecipeConfig.honeyBlockRecipes) {
            HoneyRegistry.get().getStreamOfHoney()
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

        ResourcefulBeesAPI.getRegistry().getBeeRegistry()
            .getStreamOfBees()
            .map(this::makeHiveRecipe)
            .filter(Objects::nonNull)
            .forEach(this::addRecipe);

        ResourcefulBeesAPI.getRegistry().getBeeRegistry().getFamilyTree().values().forEach(c -> c.forEach(f -> addRecipe(makeBreedingRecipe(c))));
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
        ModConstants.LOGGER.info("Adding Reload Listener: 'resourcefulbees recipe manager'");
    }

    private Recipe<?> makeBreedingRecipe(WeightedCollection<FamilyUnit> families) {
        Parents parents = families.get(0).getParents();
        ResourceLocation id = new ResourceLocation(ModConstants.MOD_ID, parents.getParent1() + "_" + parents.getParent2() + "_" + families.get(0).getChild());
        ResourceLocation parent1Id = parents.getParent1Data().id();
        BeeJarIngredient beeJarParent1 = new BeeJarIngredient(parent1Id, parents.getParent1Data().getRenderData().colorData().jarColor().getValue());
        BeeBreedData parent1BreedData = parents.getParent1Data().getBreedData();
        var parent1FeedItems = Ingredient.of(parent1BreedData.feedItems().stream().map(ItemStack::new));
        BreederRecipe.BreederPair parent1 = new BreederRecipe.BreederPair(beeJarParent1, Optional.of(parent1Id.toString()), parent1FeedItems, parent1BreedData.feedReturnItem());
        ResourceLocation parent2Id = parents.getParent2Data().id();
        BeeBreedData parent2BreedData = parents.getParent2Data().getBreedData();
        BeeJarIngredient beeJarParent2 = new BeeJarIngredient(parent2Id, parents.getParent2Data().getRenderData().colorData().jarColor().getValue());
        var parent2FeedItems = Ingredient.of(parent2BreedData.feedItems().stream().map(ItemStack::new));
        BreederRecipe.BreederPair parent2 = new BreederRecipe.BreederPair(beeJarParent2, Optional.of(parent2Id.toString()),  parent2FeedItems, parent2BreedData.feedReturnItem());
        return new BreederRecipe(id, parent1, parent2, Optional.of(Ingredient.of(ModItems.BEE_JAR.get())), families.stream().map(this::makeOutput).collect(WeightedCollection.getCollector(BreederRecipe.BreederOutput::weight)), 2400);
    }

    private BreederRecipe.BreederOutput makeOutput(FamilyUnit family) {
        ItemStack childBeeJar = BeeJarItem.createFilledJar(family.getChildData().id(), family.getChildData().getRenderData().colorData().jarColor());
        return new BreederRecipe.BreederOutput(childBeeJar, Optional.of(family.getChildData().id().toString()), family.weight(), family.chance());
    }

    private Recipe<?> makeHiveRecipe(CustomBeeData bee) {
        var optData = bee.getCoreData().getHoneycombData();
        if (optData.isEmpty()) return null;
        var data = optData.get();

        return new HiveRecipe(
            new ResourceLocation(ModConstants.MOD_ID, bee.name().toLowerCase(Locale.ROOT) + "_hive_output"),
            HolderSet.direct(EntityType::builtInRegistryHolder, bee.entityType()),
            data.hiveCombs(),
            data.apiaryCombs()
        );
    }

    private Recipe<?> makeHoneycombRecipe(CustomHoneycombItem comb) {
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

    private Recipe<?> makeHoneyBlockRecipe(CustomHoneyData info) {
        Ingredient honeyBottleItem = Ingredient.of(info.getBottleData().bottle().get());
        return new ShapedRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_honey_block"),
                "",
                2,
                2,
                NonNullList.of(Ingredient.EMPTY,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem
                ),
                new ItemStack(info.getBlockData().blockItem().get())
        );
    }

    private Recipe<?> makeBottleToBucketRecipe(CustomHoneyData info) {
        Ingredient honeyBottleItem = Ingredient.of(info.getBottleData().bottle().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_bottle_to_bucket"),
                "",
                new ItemStack(info.getFluidData().fluidBucket().get()),
                NonNullList.of(Ingredient.EMPTY,
                        bucketItem, honeyBottleItem,
                        honeyBottleItem, honeyBottleItem,
                        honeyBottleItem
                )
        );
    }

    private Recipe<?> makeBucketToBottleRecipe(CustomHoneyData info) {
        Ingredient honeyBucketItem = Ingredient.of(info.getFluidData().fluidBucket().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_bucket_to_bottle"),
                "",
                new ItemStack(info.getBottleData().bottle().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        bottleItem, bottleItem,
                        bottleItem, bottleItem,
                        honeyBucketItem
                )
        );
    }

    private Recipe<?> makeBlockToBucketRecipe(CustomHoneyData info) {
        Ingredient honeyBlockItem = Ingredient.of(info.getBlockData().blockItem().get());
        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
        return new ShapelessRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_block_to_bucket"),
                "",
                new ItemStack(info.getFluidData().fluidBucket().get()),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bucketItem
                )
        );
    }

    private Recipe<?> makeFluidToBlockRecipe(CustomHoneyData info) {
        return new SolidificationRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_fluid_to_block"),
                new FluidStack(info.getFluidData().stillFluid().get(), 1000),
                new ItemStack(info.getBlockData().blockItem().get())
        );
    }

    private Recipe<?> makeBucketToBlockRecipe(CustomHoneyData info) {
        Ingredient honeyBucketItem = Ingredient.of(info.getFluidData().fluidBucket().get());
        return new ShapelessRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_bucket_to_block"),
                "",
                new ItemStack(info.getBlockData().blockItem().get()),
                NonNullList.of(Ingredient.EMPTY, honeyBucketItem)
        );
    }

    private Recipe<?> makeHoneyBottleRecipe(CustomHoneyData info) {
        Ingredient honeyBlockItem = Ingredient.of(info.getBlockData().blockItem().get());
        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
        return new ShapelessRecipe(
                new ResourceLocation(ModConstants.MOD_ID, info.name() + "_honey_bottle"),
                "",
                new ItemStack(info.getBottleData().bottle().get(), 4),
                NonNullList.of(Ingredient.EMPTY,
                        honeyBlockItem, bottleItem,
                        bottleItem, bottleItem,
                        bottleItem
                )
        );
    }

    private Recipe<?> makeCombBlockToCombRecipe(CustomHoneycombItem comb) {
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
