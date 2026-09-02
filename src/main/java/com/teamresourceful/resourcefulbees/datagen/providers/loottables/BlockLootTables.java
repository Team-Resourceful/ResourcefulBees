package com.teamresourceful.resourcefulbees.datagen.providers.loottables;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.providers.base.BaseBlockLootTable;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class BlockLootTables extends BaseBlockLootTable {

    public BlockLootTables(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected void generate() {
        // Misc
        dropSelf(ModBlocks.WAX_BLOCK);
        dropSelf(ModBlocks.GOLD_FLOWER);

        // Hives / nests
        ModBlocks.HIVES.getEntries().forEach(this::addNest);

        // Apiary
        dropSelf(ModBlocks.T1_APIARY_BLOCK);
        dropSelf(ModBlocks.T2_APIARY_BLOCK);
        dropSelf(ModBlocks.T3_APIARY_BLOCK);
        dropSelf(ModBlocks.T4_APIARY_BLOCK);
        dropSelf(ModBlocks.BREEDER_BLOCK);
        dropSelf(ModBlocks.FLOW_HIVE);

        // Machines
        dropSelf(ModBlocks.HONEY_GENERATOR);
        dropSelf(ModBlocks.SOLIDIFICATION_CHAMBER);
        dropSelf(ModBlocks.ENDER_BEECON);
        dropSelf(ModBlocks.HONEY_POT);

        // Waxed building blocks
        dropSelf(ModBlocks.WAXED_MACHINE_BLOCK);
        dropSelf(ModBlocks.WAXED_PLANKS);

        add(
                ModBlocks.WAXED_DOOR,
                createDoorTable(ModBlocks.WAXED_DOOR.get())
        );

        dropSelf(ModBlocks.WAXED_BUTTON);
        dropSelf(ModBlocks.WAXED_FENCE);
        dropSelf(ModBlocks.WAXED_FENCE_GATE);
        dropSelf(ModBlocks.WAXED_PRESSURE_PLATE);
        dropSelf(ModBlocks.WAXED_SLAB);
        dropSelf(ModBlocks.WAXED_STAIRS);
        dropSelf(ModBlocks.WAXED_TRAPDOOR);

        dropOther(
                ModBlocks.WAXED_SIGN.get(),
                ModItems.WAXED_SIGN.get()
        );

        dropOther(
                ModBlocks.WAXED_WALL_SIGN.get(),
                ModItems.WAXED_SIGN.get()
        );

        dropOther(
                ModBlocks.WAXED_HANGING_SIGN.get(),
                ModItems.WAXED_HANGING_SIGN.get()
        );

        dropOther(
                ModBlocks.WAXED_WALL_HANGING_SIGN.get(),
                ModItems.WAXED_HANGING_SIGN.get()
        );

        dropSelf(ModBlocks.TRIMMED_WAXED_PLANKS);

        ModBlocks.CENTRIFUGE_BLOCKS
                .getEntries()
                .forEach(this::dropSelf);

        addBeeBox(ModBlocks.BEE_BOX, false);
        addBeeBox(ModBlocks.BEE_BOX_TEMP, true);
    }

    private void addBeeBox(
            Supplier<? extends Block> box,
            boolean temporary
    ) {
        Block block = box.get();

        // TODO:
        // Restore Bee Box component/block-entity data copying for 26.2.
        //
        // Until that is implemented, dropping the box itself is safer
        // than generating no loot table at all.
        dropSelf(block);
    }

    private void addNest(
            RegistryEntry<? extends Block> nest
    ) {
        Block block = nest.get();

        // TODO:
        // Restore occupied-bee / tier data preservation for 26.2.
        //
        // Basic fallback for now.
        dropSelf(block);
    }
}