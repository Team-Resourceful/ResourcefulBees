package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.item.HoneycombBlockItem;
import com.dungeonderps.resourcefulbees.item.ResourcefulHoneycomb;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE;

@mezz.jei.api.JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new BeeHiveCategory(helper));
        registration.addRecipeCategories(new BeeBreedingCategory(helper));
        registration.addRecipeCategories(new BeeMutationCategory(helper));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(helper));
    }
    @Nonnull
    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation( ResourcefulBees.MOD_ID, "jei" );
    }

    @Override
    public void registerItemSubtypes( ISubtypeRegistration subtypeRegistry )
    {
        subtypeRegistry.registerSubtypeInterpreter( RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), honeycombSubtype );
        subtypeRegistry.registerSubtypeInterpreter( RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), honeycombBlockSubtype);
    }

    private static final ISubtypeInterpreter honeycombSubtype = stack -> {
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

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.CENTRIFUGE_ITEM.get()), CentrifugeRecipeCategory.ID);
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        World clientWorld= Minecraft.getInstance().world;
        if (clientWorld != null) {
            RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();
            Collection<CentrifugeRecipe> recipes = getRecipes(recipeManager, CENTRIFUGE_RECIPE_TYPE);
            registration.addRecipes(BeeHiveCategory.getHoneycombRecipes(registration.getIngredientManager()), BeeHiveCategory.ID);
            registration.addRecipes(recipes, CentrifugeRecipeCategory.ID);
            registration.addRecipes(BeeBreedingCategory.getBreedingRecipes(registration.getIngredientManager()), BeeBreedingCategory.ID);
            registration.addRecipes(BeeMutationCategory.getMutationRecipes(registration.getIngredientManager()), BeeMutationCategory.ID);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
        ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singletonList(RegistryHandler.RESOURCEFUL_HONEYCOMB.get().getDefaultInstance()));
    }

    private static <C extends IInventory, T extends IRecipe<C>> Collection<T> getRecipes(RecipeManager recipeManager, IRecipeType<T> recipeType) {
        Map<ResourceLocation, IRecipe<C>> recipesMap = recipeManager.getRecipes(recipeType);
        return (Collection<T>) recipesMap.values();
    }
}
