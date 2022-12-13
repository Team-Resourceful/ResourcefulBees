package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;

public class ItemLanguageModule implements LanguageModule {
    @Override
    public void addEntries(BaseLanguageProvider provider) {
        provider.addItem(ModItems.OREO_COOKIE, "Epic Oreo");
        provider.addItem(ModItems.BEE_BOX_TEMP, "Lost Bee Box");
        provider.addItem(ModItems.BEE_BOX, "Bee Box");
        provider.addItem(ModItems.BEEPEDIA, "Beepedia");
        provider.addItem(ModItems.HONEY_DIPPER, "Honey Dipper");
        provider.addItem(ModItems.SCRAPER, "Scraper");
        provider.addItem(ModItems.SMOKER, "Bee Smoker");
        provider.addItem(ModItems.BELLOW, "Bellow");
        provider.addItem(ModItems.SMOKERCAN, "Smoker Canister");
        provider.addItem(ModItems.WAX, "Beeswax");
        provider.addItem(ModItems.BREED_TIME_UPGRADE, "Breed Time Upgrade");
        provider.addItem(ModItems.HONEY_FLUID_BUCKET, "Honey Bucket");
        provider.addItem(ModItems.MUTATED_POLLEN, "Mutated Pollen");
        provider.addItem(ModItems.T2_NEST_UPGRADE, "Tier 1 Nest Upgrade");
        provider.addItem(ModItems.T3_NEST_UPGRADE, "Tier 2 Nest Upgrade");
        provider.addItem(ModItems.T4_NEST_UPGRADE, "Tier 3 Nest Upgrade");
    }
}
