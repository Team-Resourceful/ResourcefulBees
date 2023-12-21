package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public final class ModPotions {

    private ModPotions() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<Potion> POTIONS = RegistryHelper.create(BuiltInRegistries.POTION, ModConstants.MOD_ID);

    public static final RegistryEntry<Potion> CALMING_POTION = POTIONS.register("calming", () -> new Potion(new MobEffectInstance(ModEffects.CALMING.get(), 6000)));
    public static final RegistryEntry<Potion> LONG_CALMING_POTION = POTIONS.register("long_calming", () -> new Potion(new MobEffectInstance(ModEffects.CALMING.get(), 12000)));


}
