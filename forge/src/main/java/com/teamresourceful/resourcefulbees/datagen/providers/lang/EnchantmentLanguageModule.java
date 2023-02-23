package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEnchantments;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;

public class EnchantmentLanguageModule implements LanguageModule {
    @Override
    public void addEntries(BaseLanguageProvider provider) {
        provider.addEnchantment(ModEnchantments.HIVE_BREAK, "A-Pick-O");
        provider.addEnchantmentDesc(ModEnchantments.HIVE_BREAK, "Chance to spawn a hive when breaking a block of a similar type.");
    }
}
