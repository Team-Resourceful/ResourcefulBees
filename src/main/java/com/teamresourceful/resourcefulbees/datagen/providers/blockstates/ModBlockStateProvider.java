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
        registerApiary(ModBlocks.FLOW_HIVE);
        registerApiary(ModBlocks.BREEDER_BLOCK);
        registerCentrifuge();
        registerWaxedBlocks();
        //using sign generator as it does what we need and only generates a model with particle field.
        simpleBlock(ModBlocks.BEEHOUSE_TOP.get(), models().sign("bee_house_top", modLoc("block/apiary/t1_apiary")));
        simpleBlockWithItem(ModBlocks.BEE_BOX.get(), models().cubeBottomTop("bee_box", modLoc("block/bee_box_side"), modLoc("block/trimmed_waxed_planks"), modLoc("block/bee_box_top")));
        simpleBlockWithItem(ModBlocks.BEE_BOX_TEMP.get(), models().cubeBottomTop("bee_box_temp", modLoc("block/bee_box_side_temp"), modLoc("block/trimmed_waxed_planks"), modLoc("block/bee_box_top_temp")));
        simpleBlock(ModBlocks.CREATIVE_GEN.get(), cubeAll(ModBlocks.CREATIVE_GEN.get()));
        simpleBlock(ModBlocks.GOLD_FLOWER.get(), models().cross(id(ModBlocks.GOLD_FLOWER.get()), modLoc("block/gold_flower")).renderType("cutout"));
    }

    private void registerWaxedBlocks() {
        simpleBlockWithItem(ModBlocks.HONEY_GLASS_PLAYER.get(), models().cubeAll("honey_glass_player", mcLoc("block/honey_block_bottom")).renderType("translucent"));
        simpleBlockWithItem(ModBlocks.HONEY_GLASS.get(), models().cubeAll("honey_glass", mcLoc("block/honey_block_bottom")).renderType("translucent"));
        simpleBlockWithItem(ModBlocks.WAXED_PLANKS.get(), models().cubeAll("waxed_planks", modLoc("block/waxed_planks")));
        simpleBlockWithItem(ModBlocks.TRIMMED_WAXED_PLANKS.get(), models().cubeAll("trimmed_waxed_planks", modLoc("block/trimmed_waxed_planks")));
        simpleBlockWithItem(ModBlocks.WAXED_MACHINE_BLOCK.get(), models().cubeAll("waxed_machine_block", modLoc("block/waxed_machine_block")));
        buttonBlockWithItem(ModBlocks.WAXED_BUTTON.get(), modLoc("block/waxed_planks"));
        doorBlock(ModBlocks.WAXED_DOOR.get(), modLoc("block/waxed_door_bottom"), modLoc("block/waxed_door_top"));
        fenceBlockWithItem(ModBlocks.WAXED_FENCE.get(), modLoc("block/waxed_planks"));
        fenceGateBlockWithItem(ModBlocks.WAXED_FENCE_GATE.get(), modLoc("block/waxed_planks"));
        preasurePlateBlockWithItem(ModBlocks.WAXED_PRESSURE_PLATE.get(), modLoc("block/waxed_planks"));
        slabBlockWithItem(ModBlocks.WAXED_SLAB.get(), modLoc("block/waxed_planks"));
        trapdoorBlockWithItem(ModBlocks.WAXED_TRAPDOOR.get(), modLoc("block/waxed_trapdoor"));
        stairBlockWithItem(ModBlocks.WAXED_STAIRS.get(), modLoc("block/waxed_planks"));
        signBlock(ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get(), modLoc("block/waxed_planks"));
        simpleBlockWithItem(ModBlocks.WAX_BLOCK.get(), cubeAll(ModBlocks.WAX_BLOCK.get()));
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
        this.simpleBlockItem(registryObject.get(), model);
    }

    private void registerBreeder() {
        String name = ModBlocks.BREEDER_BLOCK.getId().getPath();
        ModelFile model = models().getBuilder(name)
                .parent(models().getExistingFile(modLoc("block/beehouse")))
                .texture("particle", modLoc("block/breeder"))
                .texture("texture", modLoc("block/breeder"))
                .renderType("cutout");
        getVariantBuilder(ModBlocks.BREEDER_BLOCK.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(model)
                .rotationY(((int)(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180 ) % 360))
                .build()
        );
        this.simpleBlockItem(ModBlocks.BREEDER_BLOCK.get(), model);
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
