package com.teamresourceful.resourcefulbees.datagen.providers.loottables;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseBlockLootTable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyBlockState;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraftforge.fml.RegistryObject;

public class BlockLootTables extends BaseBlockLootTable {

    @Override
    protected void addTables() {
        //region Misc
        dropSelf(ModBlocks.WAX_BLOCK);
        dropSelf(ModBlocks.GOLD_FLOWER);
        //endregion
        ModBlocks.NEST_BLOCKS.getEntries().forEach(this::addNest);
        //region Apiary
        dropSelf(ModBlocks.T1_APIARY_BLOCK);
        dropSelf(ModBlocks.T2_APIARY_BLOCK);
        dropSelf(ModBlocks.T3_APIARY_BLOCK);
        dropSelf(ModBlocks.T4_APIARY_BLOCK);
        dropSelf(ModBlocks.APIARY_STORAGE_BLOCK);
        dropSelf(ModBlocks.APIARY_BREEDER_BLOCK);
        //endregion
        //region Machines
        dropSelf(ModBlocks.HONEY_GENERATOR);
        dropSelf(ModBlocks.HONEY_CONGEALER);
        dropSelf(ModBlocks.ENDER_BEECON);
        dropSelf(ModBlocks.HONEY_POT);
        //endregion
        ModBlocks.CENTRIFUGE_BLOCKS.getEntries().forEach(this::dropSelf);
    }

    private void addNest(RegistryObject<Block> nest) {
        Block block = nest.get();
        add(block, LootTable.lootTable().withPool(getNestPool(block)));
    }

    private LootPool.Builder getNestPool(Block block) {
        return LootPool.lootPool()
                .setRolls(ConstantRange.exactly(1))
                .add(
                    ItemLootEntry.lootTableItem(block)
                        .when(HAS_SILK_TOUCH)
                        .apply(
                            CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
                                .copy("Bees", "BlockEntityTag.Bees")
                                .copy("Tier", "BlockEntityTag.Tier")
                                .copy("TierModifier", "BlockEntityTag.TierModifier")
                        )
                        .apply(
                            CopyBlockState.copyState(block).copy(BeehiveBlock.HONEY_LEVEL)
                        )
                        .otherwise(
                            ItemLootEntry.lootTableItem(block).apply(
                                CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("Tier", "BlockEntityTag.Tier").copy("TierModifier", "BlockEntityTag.TierModifier")
                            )
                        )
                );
    }
}
