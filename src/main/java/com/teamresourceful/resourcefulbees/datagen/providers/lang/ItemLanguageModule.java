package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;

public class ItemLanguageModule extends LanguageModule {
    @Override
    public void addEntries(BaseLanguageProvider provider) {
        provider.addItem(ModItems.OREO_COOKIE, "Epic Oreo");
        provider.addItem(ModItems.CRAFTING_BEE_BOX, "Lost Bee Box");
        provider.addItem(ModItems.BEE_BOX, "Bee Box");
        provider.addItem(ModItems.BEEPEDIA, "Beepedia");
        provider.addItem(ModItems.HONEY_DIPPER, "Honey Dipper");
        provider.addItem(ModItems.SCRAPER, "Scraper");
        provider.addItem(ModItems.SMOKER, "Bee Smoker");
        provider.addItem(ModItems.BELLOW, "Bellow");
        provider.addItem(ModItems.SMOKERCAN, "Smoker Canister");
        provider.addItem(ModItems.WAX, "Beeswax");
        provider.add("item.resourcefulbees.bee_jar_empty", "Empty Bee Jar");
        provider.add("item.resourcefulbees.bee_jar_filled", "Filled Bee Jar");
        provider.addItem(ModItems.APIARY_BREEDER_UPGRADE, "Breeder Upgrade");
        provider.addItem(ModItems.APIARY_BREED_TIME_UPGRADE, "Breed Time Upgrade");
        provider.addItem(ModItems.HONEY_FLUID_BUCKET, "Honey Bucket");
        provider.addItem(ModItems.T1_HIVE_UPGRADE, "Tier 1 Nest Upgrade");
        provider.addItem(ModItems.T2_HIVE_UPGRADE, "Tier 2 Nest Upgrade");
        provider.addItem(ModItems.T3_HIVE_UPGRADE, "Tier 3 Nest Upgrade");
        provider.addItem(ModItems.T4_HIVE_UPGRADE, "Tier 4 Nest Upgrade");
    }
}
