package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.centrifuge.common.blocks.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public final class CentrifugeBlocks {

    public static final ResourcefulRegistry<Block> CENTRIFUGE_BLOCKS = ResourcefulRegistries.create(ModBlocks.BLOCKS);

    public static final BlockBehaviour.Properties CENTRIFUGE_PROPERTIES = BlockBehaviour.Properties.of(Material.METAL).strength(2).sound(SoundType.METAL);

    //region Centrifuge Multiblock Blocks
    public static final RegistryEntry<Block> CENTRIFUGE_CASING = CENTRIFUGE_BLOCKS.register("centrifuge/casing", () -> new CentrifugeCasing(CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> CENTRIFUGE_PROCESSOR = CENTRIFUGE_BLOCKS.register("centrifuge/processor", () -> new CentrifugeProcessor(CENTRIFUGE_PROPERTIES));
    public static final RegistryEntry<Block> CENTRIFUGE_GEARBOX = CENTRIFUGE_BLOCKS.register("centrifuge/gearbox", () -> new CentrifugeGearbox(CENTRIFUGE_PROPERTIES));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/basic", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_BASIC_TERMINAL_ENTITY, CentrifugeTier.BASIC));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/advanced", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_TERMINAL_ENTITY, CentrifugeTier.ADVANCED));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/elite", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ELITE_TERMINAL_ENTITY, CentrifugeTier.ELITE));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_TERMINAL = CENTRIFUGE_BLOCKS.register("centrifuge/terminal/ultimate", () -> new CentrifugeTerminal(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_TERMINAL_ENTITY, CentrifugeTier.ULTIMATE));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/basic", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_BASIC_VOID_ENTITY, CentrifugeTier.BASIC));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/advanced", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_VOID_ENTITY, CentrifugeTier.ADVANCED));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/elite", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ELITE_VOID_ENTITY, CentrifugeTier.ELITE));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_VOID = CENTRIFUGE_BLOCKS.register("centrifuge/void/ultimate", () -> new CentrifugeVoid(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_VOID_ENTITY, CentrifugeTier.ULTIMATE));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/basic", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_BASIC_INPUT_ENTITY, CentrifugeTier.BASIC));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/advanced", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_INPUT_ENTITY, CentrifugeTier.ADVANCED));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/elite", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ELITE_INPUT_ENTITY, CentrifugeTier.ELITE));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_INPUT = CENTRIFUGE_BLOCKS.register("centrifuge/input/item/ultimate", () -> new CentrifugeInput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_INPUT_ENTITY, CentrifugeTier.ULTIMATE));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/basic", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_BASIC_ENERGY_PORT_ENTITY, CentrifugeTier.BASIC));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/advanced", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_ENERGY_PORT_ENTITY, CentrifugeTier.ADVANCED));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/elite", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ELITE_ENERGY_PORT_ENTITY, CentrifugeTier.ELITE));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_ENERGY_PORT = CENTRIFUGE_BLOCKS.register("centrifuge/input/energy/ultimate", () -> new CentrifugeEnergyPort(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_ENERGY_PORT_ENTITY, CentrifugeTier.ULTIMATE));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/basic", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_BASIC_ITEM_OUTPUT_ENTITY, CentrifugeTier.BASIC));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/advanced", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_ITEM_OUTPUT_ENTITY, CentrifugeTier.ADVANCED));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/elite", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ELITE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ELITE));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/item/ultimate", () -> new CentrifugeItemOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE));

    public static final RegistryEntry<Block> CENTRIFUGE_BASIC_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/basic", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_BASIC_FLUID_OUTPUT_ENTITY, CentrifugeTier.BASIC));
    public static final RegistryEntry<Block> CENTRIFUGE_ADVANCED_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/advanced", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ADVANCED_FLUID_OUTPUT_ENTITY, CentrifugeTier.ADVANCED));
    public static final RegistryEntry<Block> CENTRIFUGE_ELITE_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/elite", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ELITE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ELITE));
    public static final RegistryEntry<Block> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT = CENTRIFUGE_BLOCKS.register("centrifuge/output/fluid/ultimate", () -> new CentrifugeFluidOutput(CENTRIFUGE_PROPERTIES, CentrifugeBlockEntities.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT_ENTITY, CentrifugeTier.ULTIMATE));
    //endregion

    private CentrifugeBlocks() {
        throw new UtilityClassError();
    }
}
