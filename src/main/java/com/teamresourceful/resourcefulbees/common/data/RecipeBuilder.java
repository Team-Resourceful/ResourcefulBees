package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneycombItem;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.HolderSet;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

public final class RecipeBuilder implements ResourceManagerReloadListener {
    public static Recipe<?> makeHiveRecipe(CustomBeeData bee) {
        var optData = bee.getCoreData().getHoneycombData();
        if (optData.isEmpty()) return null;
        var data = optData.get();

        return new HiveRecipe(
                //Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, bee.name().toLowerCase(Locale.ROOT) + "_hive_output"),
                HolderSet.direct(EntityType::builtInRegistryHolder, bee.entityType()),
                data.hiveCombs(),
                data.apiaryCombs()
        );
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {

//        if (RecipeConfig.honeycombBlockRecipes) {
//            ModConstants.LOGGER.info("Generating comb recipes for {} honeycombs...", ModItems.HONEYCOMB_ITEMS.getEntries().size());
//            ModItems.HONEYCOMB_ITEMS.getEntries().stream()
//                .map(RegistryEntry::get)
//                .filter(item -> item instanceof CustomHoneycombItem)
//                .map(item -> (CustomHoneycombItem) item)
//                .filter(CustomHoneycombItem::hasStorageBlockItem)
//                .flatMap(comb -> Stream.of(makeHoneycombRecipe(comb), makeCombBlockToCombRecipe(comb)))
//                .forEach(this::addRecipe);
//        }
//
//        if (RecipeConfig.honeyBlockRecipes) {
//            HoneyRegistry.get().getStreamOfHoney()
//                .flatMap(data ->
//                    Stream.of(
//                        makeHoneyBlockRecipe(data),
//                        makeHoneyBottleRecipe(data),
//                        makeBottleToBucketRecipe(data),
//                        makeBucketToBottleRecipe(data),
//                        makeBlockToBucketRecipe(data),
//                        makeBucketToBlockRecipe(data),
//                        makeFluidToBlockRecipe(data)
//                    )
//                )
//                .forEach(this::addRecipe);
//        }

//        ResourcefulBeesAPI.getRegistry().getBeeRegistry()
//            .getStreamOfBees()
//            .map(this::makeHiveRecipe)
//            .filter(Objects::nonNull)
//            .forEach(this::addRecipe);

//        ResourcefulBeesAPI.getRegistry().getBeeRegistry().getFamilyTree().values().forEach(c -> c.forEach(f -> addRecipe(makeBreedingRecipe(c))));
    }

//    public void addRecipe(Recipe<?> recipe) {
//        getRecipeManager().getRecipes().computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(recipe.getId(), recipe);
//    }

    private Recipe<?> makeBreedingRecipe(WeightedCollection<FamilyUnit> families) {
        return null;
//        Parents parents = families.get(0).getParents();
//        Identifier id = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, parents.getParent1() + "_" + parents.getParent2() + "_" + families.get(0).getChild());
//        Ingredient beeJarParent1 = com.teamresourceful.resourcefullib.common.recipe.ingredient.IngredientHelper.getIngredient(new BeeJarIngredient(Set.of(parents.getParent1Data().id().toString())));
//        BeeBreedData parent1BreedData = parents.getParent1Data().getBreedData();
//        var parent1FeedItems = IngredientUtils.of(parent1BreedData.feedItems());
//        BreederRecipe.BreederPair parent1 = new BreederRecipe.BreederPair(beeJarParent1, Optional.of(parents.getParent1Data().id().toString()), parent1BreedData.feedAmount(), parent1FeedItems, parent1BreedData.feedReturnItem());
//        BeeBreedData parent2BreedData = parents.getParent2Data().getBreedData();
//        Ingredient beeJarParent2 = com.teamresourceful.resourcefullib.common.recipe.ingredient.IngredientHelper.getIngredient(new BeeJarIngredient(Set.of(parents.getParent2Data().id().toString())));
//        var parent2FeedItems = IngredientUtils.of(parent2BreedData.feedItems());
//        BreederRecipe.BreederPair parent2 = new BreederRecipe.BreederPair(beeJarParent2, Optional.of(parents.getParent2Data().id().toString()), parent2BreedData.feedAmount(), parent2FeedItems, parent2BreedData.feedReturnItem());
//        return new BreederRecipe(id, parent1, parent2, Optional.of(Ingredient.of(ModItems.BEE_JAR.get())), families.stream().map(this::makeOutput).collect(WeightedCollection.getCollector(BreederRecipe.BreederOutput::weight)), 2400);
    }

//    private BreederRecipe.BreederOutput makeOutput(FamilyUnit family) {
//        ItemStack childBeeJar = BeeJarItem.createFilledJar(family.getChildData().id(), family.getChildData().getRenderData().colorData().jarColor());
//        return new BreederRecipe.BreederOutput(childBeeJar, Optional.of(family.getChildData().id().toString()), family.weight(), family.chancea
//    }

    private Recipe<?> makeHoneycombRecipe(CustomHoneycombItem comb) {
        return null;
//        Ingredient honeycombItem = Ingredient.of(comb);
//        return new ShapedRecipe(
//            Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(comb.getStorageBlockItem())),
//            "",
//            CraftingBookCategory.MISC,
//            2,
//            2,
//            NonNullList.of(Ingredient.EMPTY,
//                honeycombItem, honeycombItem,
//                honeycombItem, honeycombItem
//            ),
//            new ItemStack(comb.getStorageBlockItem())
//        );
    }

//    private Recipe<?> makeHoneyBlockRecipe(CustomHoneyData info) {
//        Ingredient honeyBottleItem = IngredientUtils.of(info.getBottleData().bottle());
//        return new ShapedRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_honey_block"),
//            "",
//            CraftingBookCategory.MISC,
//            2,
//            2,
//            NonNullList.of(Ingredient.EMPTY,
//                honeyBottleItem, honeyBottleItem,
//                honeyBottleItem, honeyBottleItem
//            ),
//            new ItemStack(info.getBlockData().blockItem().get())
//        );
//    }
//
//    private Recipe<?> makeBottleToBucketRecipe(CustomHoneyData info) {
//        Ingredient honeyBottleItem = IngredientUtils.of(info.getBottleData().bottle());
//        return new ShapelessRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_bottle_to_bucket"),
//            "",
//            CraftingBookCategory.MISC,
//            new ItemStack(info.getFluidData().fluidBucket().get()),
//            NonNullList.of(Ingredient.EMPTY,
//                Ingredient.of(Items.BUCKET), honeyBottleItem,
//                honeyBottleItem, honeyBottleItem,
//                honeyBottleItem
//            )
//        );
//    }
//
//    private Recipe<?> makeBucketToBottleRecipe(CustomHoneyData info) {
//        Ingredient honeyBucketItem = IngredientUtils.of(info.getFluidData().fluidBucket());
//        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
//        return new ShapelessRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_bucket_to_bottle"),
//            "",
//            CraftingBookCategory.MISC,
//            new ItemStack(info.getBottleData().bottle().get(), 4),
//            NonNullList.of(Ingredient.EMPTY,
//                bottleItem, bottleItem,
//                bottleItem, bottleItem,
//                honeyBucketItem
//            )
//        );
//    }
//
//    private Recipe<?> makeBlockToBucketRecipe(CustomHoneyData info) {
//        Ingredient honeyBlockItem = IngredientUtils.of(info.getBlockData().blockItem());
//        Ingredient bucketItem = Ingredient.of(Items.BUCKET);
//        return new ShapelessRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_block_to_bucket"),
//            "",
//            CraftingBookCategory.MISC,
//            new ItemStack(info.getFluidData().fluidBucket().get()),
//            NonNullList.of(honeyBlockItem, bucketItem
//            )
//        );
//    }
//
//    private Recipe<?> makeFluidToBlockRecipe(CustomHoneyData info) {
//        return new SolidificationRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_fluid_to_block"),
//            new RecipeFluid(info.getFluidData().stillFluid().get()),
//            new ItemStack(info.getBlockData().blockItem().get())
//        );
//    }
//
//    private Recipe<?> makeBucketToBlockRecipe(CustomHoneyData info) {
//        Ingredient honeyBucketItem = IngredientUtils.of(info.getFluidData().fluidBucket());
//        return new ShapelessRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_bucket_to_block"),
//            "",
//            CraftingBookCategory.MISC,
//            new ItemStack(info.getBlockData().blockItem().get()),
//            NonNullList.of(Ingredient.EMPTY, honeyBucketItem)
//        );
//    }
//
//    private Recipe<?> makeHoneyBottleRecipe(CustomHoneyData info) {
//        Ingredient honeyBlockItem = IngredientUtils.of(info.getBlockData().blockItem());
//        Ingredient bottleItem = Ingredient.of(Items.GLASS_BOTTLE);
//        return new ShapelessRecipe(
//            Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, info.name() + "_honey_bottle"),
//            "",
//            CraftingBookCategory.MISC,
//            new ItemStack(info.getBottleData().bottle().get(), 4),
//            NonNullList.of(Ingredient.EMPTY,
//                honeyBlockItem, bottleItem,
//                bottleItem, bottleItem,
//                bottleItem
//            )
//        );
//    }
//
//    private Recipe<?> makeCombBlockToCombRecipe(CustomHoneycombItem comb) {
//        return new ShapelessRecipe(
//            Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(comb)),
//            "",
//            CraftingBookCategory.MISC,
//            new ItemStack(comb, 4),
//            NonNullList.of(Ingredient.EMPTY, Ingredient.of(comb.getStorageBlockItem()))
//        );
//    }
//
//    public static RecipeManagerAccessor getRecipeManager() {
//        RecipeManagerAccessor recipeManagerInvoker = (RecipeManagerAccessor) recipeManager;
//        if (!recipeManagerInvoker.getRecipes().getClass().equals(HashMap.class)) {
//            recipeManagerInvoker.setRecipes(new HashMap<>(recipeManagerInvoker.getRecipes()));
//            recipeManagerInvoker.getRecipes().replaceAll((t, v) -> new HashMap<>(recipeManagerInvoker.getRecipes().get(t)));
//        }
//
//        return recipeManagerInvoker;
//    }

//    public static void registerReloadListeners(RegisterReloadListenerEvent event) {
//        event.register(INSTANCE);
//        setRecipeManager(event.resources().getRecipeManager());
//        //REQUIRED Check if this can be put in another place
//        //its here so it can have the items in the data before the
//        //recipes are generated.
//        if (event.resources() instanceof ReloadableServerResourcesAccessor accessor) {
//            BeeRegistry.getRegistry().regenerateCustomBeeData(((TagManagerAccessor) accessor.getTagManager()).getRegistryAccess());
//        }
//        ModConstants.LOGGER.info("Adding Reload Listener: 'resourcefulbees recipe manager'");
//    }
}
