package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.providers.base.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.base.LanguageModule;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.text.WordUtils;

public class ItemLanguageModule implements LanguageModule {
    @Override
    public void addEntries(BaseLanguageProvider provider) {
        provider.addItem(ModItems.WAX_BLOCK_ITEM, "Beeswax Block");
        provider.addItem(ModItems.GOLD_FLOWER_ITEM, "Golden Flower");
        provider.addItem(ModItems.T1_APIARY_ITEM, "Tier 1 Apiary");
        provider.addItem(ModItems.T2_APIARY_ITEM, "Tier 2 Apiary");
        provider.addItem(ModItems.T3_APIARY_ITEM, "Tier 3 Apiary");
        provider.addItem(ModItems.T4_APIARY_ITEM, "Tier 4 Apiary");


        provider.addItem(ModItems.BREEDER_ITEM, "Breeder");
        provider.addItem(ModItems.HONEY_GENERATOR_ITEM, "Honey Generator");
        provider.addItem(ModItems.ENDER_BEECON_ITEM, "Ender Beecon");
        provider.addItem(ModItems.SOLIDIFICATION_CHAMBER_ITEM, "Solidification Chamber");
        provider.addItem(ModItems.WAXED_PLANKS, "Waxed Planks");
        provider.addItem(ModItems.TRIMMED_WAXED_PLANKS, "Trimmed Waxed Planks");
        provider.addItem(ModItems.WAXED_MACHINE_BLOCK, "Waxed Machine Block");
        provider.addItem(ModItems.WAXED_SLAB, "Waxed Slab");
        provider.addItem(ModItems.WAXED_STAIRS, "Waxed Stairs");
        provider.addItem(ModItems.WAXED_PRESSURE_PLATE, "Waxed Pressure Plate");
        provider.addItem(ModItems.WAXED_BUTTON, "Waxed Button");
        provider.addItem(ModItems.WAXED_SIGN, "Waxed Sign");
        provider.addItem(ModItems.WAXED_HANGING_SIGN, "Waxed Hanging Sign");
        provider.addItem(ModItems.WAXED_DOOR, "Waxed Door");
        provider.addItem(ModItems.WAXED_TRAPDOOR, "Waxed Trapdoor");
        provider.addItem(ModItems.WAXED_FENCE, "Waxed Fence");
        provider.addItem(ModItems.WAXED_FENCE_GATE, "Waxed Fence Gate");
        //provider.addItem(ModItems.FAKE_FLOWER, "Fake Golden Flower");
        provider.addItem(ModItems.HONEY_BUCKET, "Honey Bucket");
        provider.addItem(ModItems.HONEY_POT_ITEM, "Honey Pot");
        provider.addItem(ModItems.CENTRIFUGE, "Centrifuge");
        provider.addItem(ModItems.CENTRIFUGE_CRANK, "Centrifuge Crank");
        provider.addItem(ModItems.FLOW_HIVE, "Flow Hive");
        provider.addItem(ModItems.HONEY_GLASS, "§6Honey Glass (Bee)");
        provider.addItem(ModItems.HONEY_GLASS_PLAYER, "§6Honey Glass (Player)");






        provider.addItem(ModItems.OREO_COOKIE, "Epic Oreo");
        provider.addItem(ModItems.BEE_BOX_TEMP, "Lost Bee Box");
        provider.addItem(ModItems.BEE_BOX, "Bee Box");
        //provider.addItem(ModItems.BEEPEDIA, "Beepedia");
        provider.addItem(ModItems.HONEY_DIPPER, "Honey Dipper");
        provider.addItem(ModItems.SCRAPER, "Scraper");
        provider.addItem(ModItems.SMOKER, "Bee Smoker");
        provider.addItem(ModItems.BELLOW, "Bellow");
        provider.addItem(ModItems.SMOKER_CAN, "Smoker Canister");
        provider.addItem(ModItems.WAX, "Beeswax");
        provider.addItem(ModItems.BREED_TIME_UPGRADE, "Breed Time Upgrade");
        //provider.addItem(ModItems.HONEY_BUCKET, "Honey Bucket");
        //provider.addItem(ModItems.MUTATED_POLLEN, "Mutated Pollen");
        provider.addItem(ModItems.T2_NEST_UPGRADE, "Tier 2 Nest Upgrade");
        provider.addItem(ModItems.T3_NEST_UPGRADE, "Tier 3 Nest Upgrade");
        provider.addItem(ModItems.T4_NEST_UPGRADE, "Tier 4 Nest Upgrade");
        provider.addItem(ModItems.ENERGY_CAP_UPGRADE, "Energy Capacity Upgrade (WIP)");
        provider.addItem(ModItems.ENERGY_XFER_UPGRADE, "Energy Transfer Upgrade (WIP)");
        provider.addItem(ModItems.ENERGY_FILL_UPGRADE, "Energy Conversion Upgrade (WIP)");
        provider.addItem(ModItems.HONEY_CAP_UPGRADE, "Honey Capacity Upgrade (WIP)");
        provider.addItem(ModItems.BEE_LOCATOR, "Bee Locator");
        provider.addItem(ModItems.STRAWBEERRY_MILKSHAKE, "Strawbeerry Milkshake");

        ModItems.NEST_ITEMS.getEntries().forEach(registryObject -> addNest(provider, registryObject));
    }

    private void addNest(BaseLanguageProvider provider, RegistryEntry<Item> registryObject) {
        String[] ids = registryObject.getId().getPath().split("/");
        //noinspection deprecation
        String name = WordUtils.capitalize(ids[1].replace("_", " "));
        String tier = ids[2];
        provider.addItem(registryObject, "Tier %s %s Nest".formatted(tier, name));
    }
}
