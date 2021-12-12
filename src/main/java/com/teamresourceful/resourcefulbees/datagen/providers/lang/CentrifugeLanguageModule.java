package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;

public class CentrifugeLanguageModule extends LanguageModule {

    @Override
    public void addEntries(BaseLanguageProvider provider) {
        addCentrifuge(provider);
        addCentrifugeGui(provider);
    }

    private void addCentrifugeGui(BaseLanguageProvider provider) {
//        Arrays.stream(CentrifugeTier.values()).forEach(tier -> {
//            provider.add("gui.centrifuge.input.item." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Input");
//            provider.add("gui.centrifuge.output.item." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Item Output");
//            provider.add("gui.centrifuge.output.fluid." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Fluid Output");
//            provider.add("gui.centrifuge.terminal." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Terminal");
//            provider.add("gui.centrifuge.void." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Void");
//        });
    }

    private void addCentrifuge(BaseLanguageProvider provider) {
/*
        //region blocks
        provider.addBlock(ModBlocks.CENTRIFUGE_CASING, "Centrifuge Casing");
        provider.addBlock(ModBlocks.CENTRIFUGE_PROCESSOR, "Centrifuge Processor");
        provider.addBlock(ModBlocks.CENTRIFUGE_GEARBOX, "Centrifuge Gearbox");

        //terminal
        provider.addBlock(ModBlocks.CENTRIFUGE_BASIC_TERMINAL, "Basic Centrifuge Terminal");
        provider.addBlock(ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL, "Advanced Centrifuge Terminal");
        provider.addBlock(ModBlocks.CENTRIFUGE_ELITE_TERMINAL, "Elite Centrifuge Terminal");
        provider.addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL, "Ultimate Centrifuge Terminal");

        //void
        provider.addBlock(ModBlocks.CENTRIFUGE_BASIC_VOID, "Basic Centrifuge Void");
        provider.addBlock(ModBlocks.CENTRIFUGE_ADVANCED_VOID, "Advanced Centrifuge Void");
        provider.addBlock(ModBlocks.CENTRIFUGE_ELITE_VOID, "Elite Centrifuge Void");
        provider.addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_VOID, "Ultimate Centrifuge Void");

        //input
        provider.addBlock(ModBlocks.CENTRIFUGE_BASIC_INPUT, "Basic Centrifuge Input");
        provider.addBlock(ModBlocks.CENTRIFUGE_ADVANCED_INPUT, "Advanced Centrifuge Input");
        provider.addBlock(ModBlocks.CENTRIFUGE_ELITE_INPUT, "Elite Centrifuge Input");
        provider.addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_INPUT, "Ultimate Centrifuge Input");

        //energy port
        provider.addBlock(ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT, "Basic Centrifuge Energy Port");
        provider.addBlock(ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT, "Advanced Centrifuge Energy Port");
        provider.addBlock(ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT, "Elite Centrifuge Energy Port");
        provider.addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT, "Ultimate Centrifuge Energy Port");

        //item output
        provider.addBlock(ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT, "Basic Centrifuge Item Output");
        provider.addBlock(ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT, "Advanced Centrifuge Item Output");
        provider.addBlock(ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT, "Elite Centrifuge Item Output");
        provider.addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT, "Ultimate Centrifuge Item Output");

        //fluid output
        provider.addBlock(ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT, "Basic Centrifuge Fluid Output");
        provider.addBlock(ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT, "Advanced Centrifuge Fluid Output");
        provider.addBlock(ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT, "Elite Centrifuge Fluid Output");
        provider.addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT, "Ultimate Centrifuge Fluid Output");
        //endregion
         */
    }
}
