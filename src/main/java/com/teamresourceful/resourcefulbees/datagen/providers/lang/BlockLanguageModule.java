package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
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
        provider.addBlock(ModBlocks.HONEY_GENERATOR, "Honey Generator");
        provider.addBlock(ModBlocks.ENDER_BEECON, "Ender Beecon");
        provider.addBlock(ModBlocks.SOLIDIFICATION_CHAMBER, "Solidification Chamber");
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

        provider.addBlock(ModBlocks.HONEY_POT, "Honey Pot");

        ModBlocks.HIVES.getEntries().forEach(registryObject -> addNest(provider, registryObject));
    }

    private void addNest(BaseLanguageProvider provider, RegistryObject<Block> registryObject) {
        String[] ids = registryObject.getId().getPath().split("/");
        //noinspection deprecation
        String name = WordUtils.capitalize(ids[1].replace("_", " "));
        String tier = ids[2];
        provider.addBlock(registryObject, String.format("Tier %s %s Nest", tier, name));
    }

}
