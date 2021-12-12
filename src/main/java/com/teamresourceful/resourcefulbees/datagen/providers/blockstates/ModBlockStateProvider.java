package com.teamresourceful.resourcefulbees.datagen.providers.blockstates;

import com.teamresourceful.resourcefulbees.datagen.bases.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BaseBlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerCentrifuge();
    }

    private void registerCentrifuge() {
        /*
        simpleBlockWithItem(ModBlocks.CENTRIFUGE_CASING.get());
        simpleBlockWithItem(ModBlocks.CENTRIFUGE_GEARBOX.get());
        simpleBlockWithItem(ModBlocks.CENTRIFUGE_PROCESSOR.get());

        //terminal
        horizontalCentrifuge(ModBlocks.CENTRIFUGE_BASIC_TERMINAL.get());
        horizontalCentrifuge(ModBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get());
        horizontalCentrifuge(ModBlocks.CENTRIFUGE_ELITE_TERMINAL.get());
        horizontalCentrifuge(ModBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get());

        //void
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_BASIC_VOID.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ADVANCED_VOID.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ELITE_VOID.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ULTIMATE_VOID.get());

        //input
        orientableVerticalWithItem(ModBlocks.CENTRIFUGE_BASIC_INPUT.get());
        orientableVerticalWithItem(ModBlocks.CENTRIFUGE_ADVANCED_INPUT.get());
        orientableVerticalWithItem(ModBlocks.CENTRIFUGE_ELITE_INPUT.get());
        orientableVerticalWithItem(ModBlocks.CENTRIFUGE_ULTIMATE_INPUT.get());

        //energy
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get());

        //item output
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get());

        //fluid output
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get());
        horizontalCentrifugeBottom(ModBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get());
         */
    }
}
