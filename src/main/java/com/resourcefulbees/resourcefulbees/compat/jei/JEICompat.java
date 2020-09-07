package com.resourcefulbees.resourcefulbees.compat.jei;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.screen.CentrifugeMultiblockScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.CentrifugeScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.MechanicalCentrifugeScreen;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredient;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredientFactory;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityIngredientHelper;
import com.resourcefulbees.resourcefulbees.compat.jei.ingredients.EntityRenderer;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE;

@mezz.jei.api.JeiPlugin
public class JEICompat implements IModPlugin {

/*    private static final ISubtypeInterpreter honeycombSubtype = stack -> {
        Item item = stack.getItem();
        if( !(item instanceof ResourcefulHoneycomb) ) return "";
        ResourcefulHoneycomb comb = (ResourcefulHoneycomb) item;
        return comb.getTranslationKey(stack);
    };
    private static final ISubtypeInterpreter honeycombBlockSubtype = stack -> {
        Item item = stack.getItem();
        if( !(item instanceof HoneycombBlockItem) ) return "";

        HoneycombBlockItem combBlock = (HoneycombBlockItem) item;

        return combBlock.getTranslationKey(stack);
    };
    private static final ISubtypeInterpreter beeSpawnEggsSubtype = stack -> {
        Item item = stack.getItem();
        if( !(item instanceof BeeSpawnEggItem) ) return "";

        BeeSpawnEggItem spawnEgg = (BeeSpawnEggItem) item;

        return spawnEgg.getTranslationKey(stack);
    };*/

    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new BeeHiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new FlowersCategory(helper));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(helper));
        registration.addRecipeCategories(new FluidToFluid(helper));
        registration.addRecipeCategories(new BlockToFluid(helper));
        registration.addRecipeCategories(new FluidToBlock(helper));
        registration.addRecipeCategories(new BlockToBlock(helper));
        registration.addRecipeCategories(new ApiaryCategory(helper));
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation( ResourcefulBees.MOD_ID, "jei" );
    }

/*    @Override
    public void registerItemSubtypes( ISubtypeRegistration subtypeRegistry )
    {
        subtypeRegistry.registerSubtypeInterpreter( RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), honeycombSubtype );
        subtypeRegistry.registerSubtypeInterpreter( RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), honeycombBlockSubtype);
        subtypeRegistry.registerSubtypeInterpreter( RegistryHandler.BEE_SPAWN_EGG.get(), beeSpawnEggsSubtype);
    }*/

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T1_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T2_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T3_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T4_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T1_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T2_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T3_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.T4_APIARY_ITEM.get()), ApiaryCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.MECHANICAL_CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.CENTRIFUGE_CONTROLLER_ITEM.get()), CentrifugeRecipeCategory.ID);
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        World clientWorld= Minecraft.getInstance().world;
        if (clientWorld != null) {
            RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();
            registration.addRecipes(BeeHiveCategory.getHoneycombRecipes(registration.getIngredientManager()), BeeHiveCategory.ID);
            registration.addRecipes(recipeManager.getRecipes(CENTRIFUGE_RECIPE_TYPE).values(), CentrifugeRecipeCategory.ID);
            registration.addRecipes(BeeBreedingCategory.getBreedingRecipes(registration.getIngredientManager()), BeeBreedingCategory.ID);
            registration.addRecipes(FluidToFluid.getMutationRecipes(registration.getIngredientManager()), FluidToFluid.ID);
            registration.addRecipes(BlockToFluid.getMutationRecipes(registration.getIngredientManager()), BlockToFluid.ID);
            registration.addRecipes(FluidToBlock.getMutationRecipes(registration.getIngredientManager()), FluidToBlock.ID);
            registration.addRecipes(BlockToBlock.getMutationRecipes(registration.getIngredientManager()), BlockToBlock.ID);
            registration.addRecipes(ApiaryCategory.getHoneycombRecipes(registration.getIngredientManager()), ApiaryCategory.ID);
            registration.addRecipes(FlowersCategory.getFlowersRecipes(registration.getIngredientManager()), FlowersCategory.ID);
            registerInfoDesc(registration);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CentrifugeScreen.class, 80, 30, 18, 18, CentrifugeRecipeCategory.ID);
        registration.addRecipeClickArea(MechanicalCentrifugeScreen.class, 80, 30, 18, 18, CentrifugeRecipeCategory.ID);
        registration.addRecipeClickArea(CentrifugeMultiblockScreen.class, 88, 26, 18, 18, CentrifugeRecipeCategory.ID);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        List<EntityIngredient> entityIngredients = EntityIngredientFactory.create();
        registration.register(ENTITY_INGREDIENT, entityIngredients, new EntityIngredientHelper<>(), new EntityRenderer());
    }

/*    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
        ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singletonList(RegistryHandler.RESOURCEFUL_HONEYCOMB.get().getDefaultInstance()));
    }*/

    public void registerInfoDesc(IRecipeRegistration registration){
        for (EntityIngredient bee : EntityIngredientFactory.create()) {
            if (I18n.hasKey("info.resourcefulbees.jei." + bee.getBeeType())){
                registration.addIngredientInfo(bee, ENTITY_INGREDIENT,
                    "\u00a7e\u00a7l[" + bee.getDisplayName().getString() + "] : \u00a7r" +
                            I18n.format("info.resourcefulbees.jei." + bee.getBeeType())
                );
            }
        }
    }
}
