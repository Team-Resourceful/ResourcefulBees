package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

public class CentrifugeLangProvider extends BaseLanguageProvider {

    public CentrifugeLangProvider(DataGenerator gen) {
        super(gen, TranslationConstants.class);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Centrifuge Language Provider";
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addGui();
        super.addTranslations();
    }

    private void addGui() {
//        Arrays.stream(CentrifugeTier.values()).forEach(tier -> {
//            add("gui.centrifuge.input.item." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Input");
//            add("gui.centrifuge.output.item." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Item Output");
//            add("gui.centrifuge.output.fluid." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Fluid Output");
//            add("gui.centrifuge.terminal." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Terminal");
//            add("gui.centrifuge.void." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Void");
//        });
    }

    private void addBlocks() {
        /*
        //region blocks
        addBlock(ModBlocks.CENTRIFUGE_CASING, "Centrifuge Casing");
        addBlock(ModBlocks.CENTRIFUGE_PROCESSOR, "Centrifuge Processor");
        addBlock(ModBlocks.CENTRIFUGE_GEARBOX, "Centrifuge Gearbox");

        //terminal
        addBlock(ModBlocks.CENTRIFUGE_BASIC_TERMINAL, "Basic Centrifuge Terminal");
        addBlock(ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL, "Advanced Centrifuge Terminal");
        addBlock(ModBlocks.CENTRIFUGE_ELITE_TERMINAL, "Elite Centrifuge Terminal");
        addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL, "Ultimate Centrifuge Terminal");

        //void
        addBlock(ModBlocks.CENTRIFUGE_BASIC_VOID, "Basic Centrifuge Void");
        addBlock(ModBlocks.CENTRIFUGE_ADVANCED_VOID, "Advanced Centrifuge Void");
        addBlock(ModBlocks.CENTRIFUGE_ELITE_VOID, "Elite Centrifuge Void");
        addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_VOID, "Ultimate Centrifuge Void");

        //input
        addBlock(ModBlocks.CENTRIFUGE_BASIC_INPUT, "Basic Centrifuge Input");
        addBlock(ModBlocks.CENTRIFUGE_ADVANCED_INPUT, "Advanced Centrifuge Input");
        addBlock(ModBlocks.CENTRIFUGE_ELITE_INPUT, "Elite Centrifuge Input");
        addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_INPUT, "Ultimate Centrifuge Input");

        //energy port
        addBlock(ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT, "Basic Centrifuge Energy Port");
        addBlock(ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT, "Advanced Centrifuge Energy Port");
        addBlock(ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT, "Elite Centrifuge Energy Port");
        addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT, "Ultimate Centrifuge Energy Port");

        //item output
        addBlock(ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT, "Basic Centrifuge Item Output");
        addBlock(ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT, "Advanced Centrifuge Item Output");
        addBlock(ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT, "Elite Centrifuge Item Output");
        addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT, "Ultimate Centrifuge Item Output");

        //fluid output
        addBlock(ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT, "Basic Centrifuge Fluid Output");
        addBlock(ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT, "Advanced Centrifuge Fluid Output");
        addBlock(ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT, "Elite Centrifuge Fluid Output");
        addBlock(ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT, "Ultimate Centrifuge Fluid Output");
        //endregion
         */
    }
}
