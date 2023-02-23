package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.text.WordUtils;

public class BlockLanguageModule implements LanguageModule {

    @Override
    public void addEntries(BaseLanguageProvider provider) {
        provider.addBlock(ModBlocks.WAX_BLOCK, "Beeswax Block");
        provider.addBlock(ModBlocks.GOLD_FLOWER, "Golden Flower");
        provider.addBlock(ModBlocks.T1_APIARY_BLOCK, "Tier 1 Apiary");
        provider.addBlock(ModBlocks.T2_APIARY_BLOCK, "Tier 2 Apiary");
        provider.addBlock(ModBlocks.T3_APIARY_BLOCK, "Tier 3 Apiary");
        provider.addBlock(ModBlocks.T4_APIARY_BLOCK, "Tier 4 Apiary");
        provider.addBlock(ModBlocks.BREEDER_BLOCK, "Breeder");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.HONEY_GENERATOR, "Honey Generator");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.ENDER_BEECON, "Ender Beecon");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.SOLIDIFICATION_CHAMBER, "Solidification Chamber");
        provider.addBlock(ModBlocks.WAXED_PLANKS, "Waxed Planks");
        provider.addBlock(ModBlocks.TRIMMED_WAXED_PLANKS, "Trimmed Waxed Planks");
        provider.addBlock(ModBlocks.WAXED_MACHINE_BLOCK, "Waxed Machine Block");
        provider.addBlock(ModBlocks.WAXED_SLAB, "Waxed Slab");
        provider.addBlock(ModBlocks.WAXED_STAIRS, "Waxed Stairs");
        provider.addBlock(ModBlocks.WAXED_PRESSURE_PLATE, "Waxed Pressure Plate");
        provider.addBlock(ModBlocks.WAXED_BUTTON, "Waxed Button");
        provider.addBlock(ModBlocks.WAXED_SIGN, "Waxed Sign");
        provider.addBlock(ModBlocks.WAXED_DOOR, "Waxed Door");
        provider.addBlock(ModBlocks.WAXED_TRAPDOOR, "Waxed Trapdoor");
        provider.addBlock(ModBlocks.WAXED_FENCE, "Waxed Fence");
        provider.addBlock(ModBlocks.WAXED_FENCE_GATE, "Waxed Fence Gate");
        provider.addBlock(ModBlocks.FAKE_FLOWER, "Fake Golden Flower");
        provider.addFluid(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids.HONEY, "Honey");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.HONEY_POT, "Honey Pot");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.BASIC_CENTRIFUGE, "Centrifuge");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.CENTRIFUGE_CRANK, "Centrifuge Crank");
        provider.addBlock(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks.FLOW_HIVE, "Flow Hive");
        provider.addBlock(ModBlocks.HONEY_GLASS, "§6Honey Glass (Bee)");
        provider.addBlock(ModBlocks.HONEY_GLASS_PLAYER, "§6Honey Glass (Player)");

        ModBlocks.HIVES.getEntries().forEach(registryObject -> addNest(provider, registryObject));
    }

    private void addNest(BaseLanguageProvider provider, RegistryEntry<Block> registryObject) {
        String[] ids = registryObject.getId().getPath().split("/");
        //noinspection deprecation
        String name = WordUtils.capitalize(ids[1].replace("_", " "));
        String tier = ids[2];
        provider.addBlock(registryObject, String.format("Tier %s %s Nest", tier, name));
    }

}
