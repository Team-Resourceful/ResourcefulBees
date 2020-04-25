package com.dungeonderps.resourcefulbees.compat.jei;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.item.BeeSpawnEggItem;
import com.dungeonderps.resourcefulbees.item.ResourcefulHoneycomb;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nonnull;
import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

@mezz.jei.api.JeiPlugin
public class JEICompat implements IModPlugin {




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
        LOGGER.warn(combStack);
        return combStack;
    };

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
