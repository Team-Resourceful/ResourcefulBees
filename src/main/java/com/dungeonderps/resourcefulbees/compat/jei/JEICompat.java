package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.item.ResourcefulHoneycomb;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@mezz.jei.api.JeiPlugin
public class JEICompat implements IModPlugin {


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new BeeHiveCategory(helper));
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
        //subtypeRegistry.registerSubtypeInterpreter( RegistryHandler.BEE_SPAWN_EGG.get(), beeSpawnEggSubtype );
    }

    private static final ISubtypeInterpreter honeycombSubtype = stack -> {
        Item item = stack.getItem();
        if( !(item instanceof ResourcefulHoneycomb) ) return "";

        ResourcefulHoneycomb comb = (ResourcefulHoneycomb) item;

        String combStack = comb.getTranslationKey(stack);
        return combStack;
    };
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get()), BeeHiveCategory.ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(BeeHiveCategory.getHoneycombRecipes(registration.getIngredientManager()), BeeHiveCategory.ID);
    }
    /*
    private static final ISubtypeInterpreter beeSpawnEggSubtype = stack -> {
        Item item = stack.getItem();
        if( !(item instanceof BeeSpawnEggItem) ) return "";

        BeeSpawnEggItem egg = (BeeSpawnEggItem) item;

        String spawnEgg = egg.getTranslationKey(stack);
        return spawnEgg;
    };
    */
}
