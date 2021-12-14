package com.teamresourceful.resourcefulbees.datagen.providers.blockstates;

import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BaseBlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModBlocks.HIVES.getEntries().forEach(this::registerNest);
        registerCentrifuge();
    }

    private void registerNest(RegistryObject<Block> registryObject) {
        String name = registryObject.getId().getPath();
        ModelFile model = buildNestModel(name, false);
        ModelFile modelHoney = buildNestModel(name, true);

        getVariantBuilder(registryObject.get()).forAllStates(state -> state.getValue(TieredBeehiveBlock.HONEY_LEVEL) == 5 ?
                ConfiguredModel.builder().modelFile(modelHoney).build() : ConfiguredModel.builder().modelFile(model).build());
        this.simpleBlockItem(registryObject.get(), model);
    }

    //TODO figure out how to use the element builder to add a tier overlay
    private ModelFile buildNestModel(String id, boolean honey) {
        String texture = id.substring(0, id.lastIndexOf("/") + 1);
        return models().getBuilder(id)
                .parent(models().getExistingFile(mcLoc("block/orientable_with_bottom")))
                .texture("particle", modLoc("block/" + texture + "side"))
                .texture("bottom", modLoc("block/" + texture + "bottom"))
                .texture("top", modLoc("block/" + texture + "top"))
                .texture("front", modLoc("block/" + texture + (honey ? "front" : "front_honey")))
                .texture("side", modLoc("block/" + texture + "side"));
    }

    private void registerCentrifuge() {
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
    }
}
