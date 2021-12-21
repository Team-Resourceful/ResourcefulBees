package com.teamresourceful.resourcefulbees.datagen.providers.blockstates;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
        ModBlocks.APIARIES.getEntries().forEach(this::registerApiary);
        registerCentrifuge();
        //using sign generator as it does what we need and only generates a model with particle field.
        simpleBlock(ModBlocks.BEEHOUSE_TOP.get(), models().sign("bee_house_top", modLoc("block/apiary/t1_apiary")));
    }

    private void registerApiary(RegistryObject<Block> registryObject) {
        String name = registryObject.getId().getPath();
        ModelFile model = models().getBuilder(name)
                .parent(models().getExistingFile(modLoc("block/beehouse")))
                .texture("particle", modLoc("block/apiary/"+name))
                .texture("texture", modLoc("block/apiary/"+name));
        getVariantBuilder(registryObject.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(model)
                .rotationY(((int)(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180 ) % 360))
                .build()
        );
    }

    private void registerNest(RegistryObject<Block> registryObject) {
        String name = registryObject.getId().getPath();
        ModelFile model = buildNestModel(name, false);
        ModelFile modelHoney = buildNestModel(name, true);

        getVariantBuilder(registryObject.get()).forAllStates(state -> getNestModel(state, modelHoney, model)
                .rotationY(((int)(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180 ) % 360))
                .build()
        );
        this.simpleBlockItem(registryObject.get(), model);
    }

    private ConfiguredModel.Builder<?> getNestModel(BlockState state, ModelFile modelHoney, ModelFile model) {
        return state.getValue(BlockStateProperties.LEVEL_HONEY) == 5 ?
                ConfiguredModel.builder().modelFile(modelHoney) : ConfiguredModel.builder().modelFile(model);
    }

    private ModelFile buildNestModel(String id, boolean honey) {
        String texture = id.substring(0, id.lastIndexOf("/") + 1);
        String tier = id.substring(id.lastIndexOf("/") + 1);
        return models().getBuilder(id)
                .parent(models().getExistingFile(modLoc("block/nest")))
                .texture("particle", modLoc("block/" + texture + "side"))
                .texture("bottom", modLoc("block/" + texture + "bottom"))
                .texture("top", modLoc("block/" + texture + "top"))
                .texture("front", modLoc("block/" + texture + (honey ? "front" : "front_honey")))
                .texture("side", modLoc("block/" + texture + "side"))
                .texture("tier", modLoc("block/nest/tier_overlay/tier_" + tier));
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
