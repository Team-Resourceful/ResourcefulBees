package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeInputScreen;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeVoidScreen;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.CentrifugeInputGhostIngredientHandler;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredientHelper;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityRenderer;
import com.teamresourceful.resourcefulbees.common.compat.jei.mutation.MutationCategory;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
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
public class JEICompat implements IModPlugin {

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    private static IJeiRuntime JEI_RUNTIME = null;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new HiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new FlowersCategory(helper));
        registration.addRecipeCategories(new MutationCategory(helper));
        registration.addRecipeCategories(new CentrifugeCategory(helper));
        registration.addRecipeCategories(new SolidificationCategory(helper));
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ResourcefulBees.MOD_ID, "jei");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.T1_APIARY_ITEM.get()), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T2_APIARY_ITEM.get()), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T3_APIARY_ITEM.get()), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.T4_APIARY_ITEM.get()), HiveCategory.RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.BREEDER_ITEM.get()), BeeBreedingCategory.RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.SOLIDIFICATION_CHAMBER_ITEM.get()), SolidificationCategory.RECIPE);
        var nests = ModItems.T1_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).map(ItemStack::new).toList();
        for (ItemStack stack : nests) {
            registration.addRecipeCatalyst(stack, HiveCategory.RECIPE);
        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            RecipeManager recipeManager = level.getRecipeManager();
            registration.addRecipes(HiveCategory.RECIPE, HiveCategory.getHoneycombRecipes(recipeManager.getAllRecipesFor(ModRecipeTypes.HIVE_RECIPE_TYPE.get())));
            registration.addRecipes(BeeBreedingCategory.RECIPE, BeeBreedingCategory.getRecipes(recipeManager.getAllRecipesFor(ModRecipeTypes.BREEDER_RECIPE_TYPE.get())));
            registration.addRecipes(MutationCategory.RECIPE, MutationCategory.getMutationRecipes(level));
            registration.addRecipes(FlowersCategory.RECIPE, FlowersCategory.getFlowersRecipes());
            registration.addRecipes(CentrifugeCategory.RECIPE, CentrifugeCategory.getRecipes(recipeManager.getAllRecipesFor(ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get())));
            registration.addRecipes(SolidificationCategory.RECIPE, recipeManager.getAllRecipesFor(ModRecipeTypes.SOLIDIFICATION_RECIPE_TYPE.get()));
        }
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
                ModItems.BEEPEDIA.get(),
                (ingredient, context) -> ingredient.hasTag() && ingredient.getTag() != null && ingredient.getTag().contains(NBTConstants.Beepedia.CREATIVE) ? "creative.beepedia" : "");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(CentrifugeInputScreen.class, new CentrifugeInputGhostIngredientHandler<>());
        registration.addGhostIngredientHandler(CentrifugeVoidScreen.class, new CentrifugeInputGhostIngredientHandler<>());
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        JEICompat.JEI_RUNTIME = runtime;
    }

    public static void searchEntity(EntityType<?> entity) {
        if (JEI_RUNTIME != null) {
            try {
                var focus = JEI_RUNTIME.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, ENTITY_INGREDIENT, new EntityIngredient(entity, -45.0f));
                JEI_RUNTIME.getRecipesGui().show(focus);
            } catch (Exception ignored) {
                //DO Nothing
            }
        }
    }
}
