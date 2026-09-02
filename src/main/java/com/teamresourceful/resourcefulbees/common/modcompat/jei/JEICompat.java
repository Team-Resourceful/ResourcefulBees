package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.recipe.RBeesClientRecipes;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityIngredientHelper;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients.EntityRenderer;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.mutation.MutationCategory;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.subsystems.JeiSubsystem;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

@JeiPlugin
public final class JEICompat implements IModPlugin {

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT =
            () -> EntityIngredient.class;

    private static IJeiRuntime jeiRuntime;

    @Override
    public @NonNull Identifier getPluginUid() {
        return ModIdentifier.of("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        IPlatformFluidHelper<FluidStack> fluidHelper = getFluidHelper(registration.getJeiHelpers());

        registration.addRecipeCategories(
                new HiveCategory(guiHelper),
                new BeeBreedingCategory(guiHelper),
                new FlowersCategory(guiHelper),
                new MutationCategory(guiHelper, fluidHelper),
                new CentrifugeCategory(registration.getJeiHelpers()),
                new SolidificationCategory(guiHelper, fluidHelper),
                new HoneyGenCategory(guiHelper, fluidHelper),
                new FlowHiveCategory(guiHelper, fluidHelper)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(
                HiveCategory.RECIPE,
                ModItems.T1_APIARY_ITEM.get(),
                ModItems.T2_APIARY_ITEM.get(),
                ModItems.T3_APIARY_ITEM.get(),
                ModItems.T4_APIARY_ITEM.get()
        );

        registration.addCraftingStation(
                BeeBreedingCategory.RECIPE,
                ModItems.BREEDER_ITEM.get()
        );

        registration.addCraftingStation(
                SolidificationCategory.RECIPE,
                ModItems.SOLIDIFICATION_CHAMBER_ITEM.get()
        );

        registration.addCraftingStation(
                HoneyGenCategory.RECIPE,
                ModItems.HONEY_GENERATOR_ITEM.get()
        );

        registration.addCraftingStation(
                FlowHiveCategory.RECIPE,
                ModItems.FLOW_HIVE.get()
        );

        ModItems.T1_NEST_ITEMS.getEntries()
                .stream()
                .map(RegistryEntry::get)
                .forEach(item ->
                        registration.addCraftingStation(
                                HiveCategory.RECIPE,
                                item
                        )
                );

        registration.addCraftingStation(
                CentrifugeCategory.RECIPE,
                ModItems.CENTRIFUGE.get(),
                ModItems.CENTRIFUGE_CRANK.get()
        );

        ServiceLoader.load(JeiSubsystem.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .forEach(system -> system.addRecipeCatalysts((ingredient, recipeTypes) -> {
                    for (IRecipeType<?> recipeType : recipeTypes) {
                        registration.addCraftingStation(recipeType, ingredient.getItem());
                    }
                }));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IJeiHelpers helpers = registration.getJeiHelpers();
        IPlatformFluidHelper<FluidStack> fluidHelper =
                getFluidHelper(helpers);

        registration.addRecipes(
                HiveCategory.RECIPE,
                HiveCategory.getHoneycombRecipes(
                        unwrapRecipes(RBeesClientRecipes.getHiveRecipes())
                )
        );

        registration.addRecipes(
                BeeBreedingCategory.RECIPE,
                BeeBreedingCategory.getRecipes(
                        unwrapRecipes(RBeesClientRecipes.getBreederRecipes())
                )
        );

        registration.addRecipes(
                MutationCategory.RECIPE,
                MutationCategory.getMutationRecipes(Minecraft.getInstance().level)
        );

        registration.addRecipes(
                FlowersCategory.RECIPE,
                FlowersCategory.getFlowersRecipes(
                        fluidHelper
                )
        );

        registration.addRecipes(
                CentrifugeCategory.RECIPE,
                CentrifugeCategory.getRecipes(
                        fluidHelper,
                        unwrapRecipes(RBeesClientRecipes.getCentrifugeRecipes())
                )
        );

        registration.addRecipes(
                SolidificationCategory.RECIPE,
                unwrapRecipes(RBeesClientRecipes.getSolidificationRecipes())
        );

        registration.addRecipes(
                HoneyGenCategory.RECIPE,
                unwrapRecipes(RBeesClientRecipes.getHoneyGeneratorRecipes())
        );

        registration.addRecipes(
                FlowHiveCategory.RECIPE,
                FlowHiveCategory.getHoneycombRecipes(unwrapRecipes(RBeesClientRecipes.getFlowHiveRecipes()))
        );

        ServiceLoader.load(JeiSubsystem.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .forEach(system ->
                        system.addExtraInfo(
                                (item, text) ->
                                        registration.addIngredientInfo(
                                                item,
                                                VanillaTypes.ITEM_STACK,
                                                text
                                        )
                        )
                );
    }

    @Override
    public void registerIngredients(
            IModIngredientRegistration registration
    ) {
        registration.register(
                ENTITY_INGREDIENT,
                BeeRegistry.get()
                        .getStreamOfBees()
                        .map(bee -> EntityIngredient.of(bee.entityType()))
                        .toList(),
                new EntityIngredientHelper(),
                new EntityRenderer(),
                EntityIngredient.CODEC
        );
    }

    @Override
    public void registerItemSubtypes(@NonNull ISubtypeRegistration registration) {
        /*
         * Port Beepedia to data components first.
         *
         * Once that component exists, use:
         *
         * registration.registerFromDataComponentTypes(
         *     ModItems.BEEPEDIA.get(),
         *     ModDataComponents.<BEEPEDIA_COMPONENT>.get()
         * );
         */
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        ServiceLoader.load(JeiSubsystem.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .forEach(system -> system.addScreenHandlers(registration::addGhostIngredientHandler));
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        jeiRuntime = null;
    }

    public static void searchEntity(EntityType<?> entityType) {
        if (jeiRuntime == null) {
            return;
        }

        var focusFactory = jeiRuntime.getJeiHelpers().getFocusFactory();
        var focus = focusFactory.createFocus(RecipeIngredientRole.INPUT, ENTITY_INGREDIENT, EntityIngredient.of(entityType));
        jeiRuntime.getRecipesGui().show(focus);
    }

    @SuppressWarnings("unchecked")
    private static IPlatformFluidHelper<FluidStack> getFluidHelper(IJeiHelpers helpers) {
        return (IPlatformFluidHelper<FluidStack>)
                helpers.getPlatformFluidHelper();
    }

    private static <T extends Recipe<?>> List<T> unwrapRecipes(
            Collection<RecipeHolder<T>> recipes
    ) {
        return recipes.stream()
                .map(RecipeHolder::value)
                .toList();
    }
}