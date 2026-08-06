package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneycombItem;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ChildOutput;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ParentInput;
import com.teamresourceful.resourcefulbees.common.recipes.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.util.IngredientUtils;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class RecipeBuilder implements ResourceManagerReloadListener {

    public static Recipe<HiveRecipe.Input> makeHiveRecipe(CustomBeeData bee) {
        return bee.getCoreData().getHoneycombData()
                .<Recipe<HiveRecipe.Input>>map(data -> new HiveRecipe(
                        HolderSet.direct(
                                BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(bee.entityType())
                        ),
                        data.hiveCombs(),
                        data.apiaryCombs()
                ))
                .orElse(null);
    }

    public static Recipe<BreederRecipe.Input> makeBreedingRecipe(WeightedCollection<FamilyUnit> family) {
        Parents parents = family.get(0).getParents();
        ParentInput parent1 = makeInput(parents.getParent1(), parents.getParent1Data().getBreedData());
        ParentInput parent2 = makeInput(parents.getParent2(), parents.getParent2Data().getBreedData());
        return new BreederRecipe(parent1, parent2, Optional.of(Ingredient.of(ModItems.BEE_JAR.get())), family.stream().map(RecipeBuilder::makeOutput).collect(WeightedCollection.getCollector(ChildOutput::weight)), 2400);
    }

    private static ParentInput makeInput(Identifier id, BeeBreedData breedData) {
        Ingredient ingredient = new BeeJarIngredient(id).toVanilla();
        var parent1FeedItems = IngredientUtils.of(breedData.feedItems());
        return new ParentInput(ingredient, Optional.of(id), breedData.feedAmount(), parent1FeedItems, breedData.feedReturnItem());
    }

    private static ChildOutput makeOutput(FamilyUnit family) {
        ItemStackTemplate childBeeJar = ItemStackTemplate.fromNonEmptyStack(BeeJarItem.createFilledJar(family.getChildData().entityType(), getJarColor(family)));
        return new ChildOutput(childBeeJar, Optional.of(family.getChildData().id().toString()), family.weight(), family.chance());
    }

    private static int getJarColor(FamilyUnit family) {
        return family.getChildData().getRenderData().colorData().jarColor().getOpaqueValue();
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
