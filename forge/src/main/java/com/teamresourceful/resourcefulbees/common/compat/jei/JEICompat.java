package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeInputScreen;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeVoidScreen;
import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.CentrifugeInputGhostIngredientHandler;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredientHelper;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityRenderer;
import com.teamresourceful.resourcefulbees.common.compat.jei.mutation.MutationCategory;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public final class JEICompat implements IModPlugin {

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    private static IJeiRuntime jeiRuntime = null;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new HiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new FlowersCategory(helper));
        registration.addRecipeCategories(new MutationCategory(helper));
        registration.addRecipeCategories(new CentrifugeCategory(helper));
        registration.addRecipeCategories(new SolidificationCategory(helper));
        registration.addRecipeCategories(new HoneyGenCategory(helper));
        registration.addRecipeCategories(new FlowHiveCategory(helper));
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModConstants.MOD_ID, "jei");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T1_APIARY_ITEM.get().getDefaultInstance(), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_APIARY_ITEM.get().getDefaultInstance(), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T3_APIARY_ITEM.get().getDefaultInstance(), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T4_APIARY_ITEM.get().getDefaultInstance(), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BREEDER_ITEM.get().getDefaultInstance(), BeeBreedingCategory.RECIPE);
        registration.addRecipeCatalyst(ModItems.SOLIDIFICATION_CHAMBER_ITEM.get().getDefaultInstance(), SolidificationCategory.RECIPE);
        registration.addRecipeCatalyst(ModItems.HONEY_GENERATOR_ITEM.get().getDefaultInstance(), HoneyGenCategory.RECIPE);
        registration.addRecipeCatalyst(ModItems.FLOW_HIVE.get().getDefaultInstance(), FlowHiveCategory.RECIPE);
        var nests = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T1_NEST_ITEMS.getEntries().stream()
                .map(RegistryEntry::get)
                .map(ItemStack::new)
                .toList();
        for (ItemStack stack : nests) {
            registration.addRecipeCatalyst(stack, HiveCategory.RECIPE);
        }
        registration.addRecipeCatalyst(ModItems.CENTRIFUGE.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(ModItems.CENTRIFUGE_CRANK.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_BASIC_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_ADVANCED_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_ELITE_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_ULTIMATE_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        //IDK if I should include so many of the blocks as catalysts
        /*registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_BASIC_TERMINAL.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_ELITE_TERMINAL.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        registration.addRecipeCatalyst(CentrifugeItems.CENTRIFUGE_ULTIMATE_TERMINAL.get().getDefaultInstance(), CentrifugeCategory.RECIPE);*/
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(HiveCategory.RECIPE, HiveCategory.getHoneycombRecipes(recipeManager.getAllRecipesFor(ModRecipes.HIVE_RECIPE_TYPE.get())));
        registration.addRecipes(BeeBreedingCategory.RECIPE, BeeBreedingCategory.getRecipes(recipeManager.getAllRecipesFor(ModRecipes.BREEDER_RECIPE_TYPE.get())));
        registration.addRecipes(MutationCategory.RECIPE, MutationCategory.getMutationRecipes(level));
        registration.addRecipes(FlowersCategory.RECIPE, FlowersCategory.getFlowersRecipes());
        registration.addRecipes(CentrifugeCategory.RECIPE, CentrifugeCategory.getRecipes(recipeManager.getAllRecipesFor(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get())));
        registration.addRecipes(SolidificationCategory.RECIPE, recipeManager.getAllRecipesFor(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get()));
        registration.addRecipes(HoneyGenCategory.RECIPE, recipeManager.getAllRecipesFor(ModRecipes.HONEY_GEN_RECIPE_TYPE.get()));
        registration.addRecipes(FlowHiveCategory.RECIPE, FlowHiveCategory.getHoneycombRecipes(recipeManager.getAllRecipesFor(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get())));
        CentrifugeInfo.register(registration);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
                ENTITY_INGREDIENT,
                BeeRegistry.get().getStreamOfBees().map(b -> new EntityIngredient(b.entityType(), -45.0f)).toList(),
                new EntityIngredientHelper(),
                new EntityRenderer()
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK,
                com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEEPEDIA.get(),
                (ingredient, context) -> ingredient.hasTag() && ingredient.getTag() != null && ingredient.getTag().contains(NBTConstants.Beepedia.CREATIVE) ? "creative.beepedia" : "");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(CentrifugeInputScreen.class, new CentrifugeInputGhostIngredientHandler<>());
        registration.addGhostIngredientHandler(CentrifugeVoidScreen.class, new CentrifugeInputGhostIngredientHandler<>());
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        JEICompat.jeiRuntime = runtime;
    }

    public static void searchEntity(EntityType<?> entity) {
        if (jeiRuntime != null) {
            try {
                var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, ENTITY_INGREDIENT, new EntityIngredient(entity, -45.0f));
                jeiRuntime.getRecipesGui().show(focus);
            } catch (Exception ignored) {
                //DO Nothing
            }
        }
    }


}
