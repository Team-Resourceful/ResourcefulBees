package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

public final class ModEnchantments {

    private ModEnchantments() throws UtilityClassException {
        throw new UtilityClassException();
    }

    //public static final ResourcefulRegistry<Enchantment> ENCHANTMENTS = RegistryHelper.create(BuiltInRegistries.ENCHANTMENT, ModConstants.MOD_ID);

    //public static final RegistryEntry<HiveBreakEnchantment> HIVE_BREAK = ENCHANTMENTS.register("hive_break", HiveBreakEnchantment::new);
}
