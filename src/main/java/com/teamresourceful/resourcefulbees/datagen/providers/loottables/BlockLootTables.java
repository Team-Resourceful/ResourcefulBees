package com.teamresourceful.resourcefulbees.datagen.providers.loottables;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseBlockLootTable;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

public class BlockLootTables extends BaseBlockLootTable {

    @Override
    protected void addTables() {
        //region Misc
        dropSelf(ModBlocks.WAX_BLOCK);
        dropSelf(ModBlocks.GOLD_FLOWER);
        //endregion
        ModBlocks.HIVES.getEntries().forEach(this::addNest);
        //region Apiary
        dropSelf(ModBlocks.T1_APIARY_BLOCK);
        dropSelf(ModBlocks.T2_APIARY_BLOCK);
        dropSelf(ModBlocks.T3_APIARY_BLOCK);
        dropSelf(ModBlocks.T4_APIARY_BLOCK);
        dropSelf(ModBlocks.APIARY_BREEDER_BLOCK);
        //endregion
        //region Machines
        dropSelf(ModBlocks.HONEY_GENERATOR);
        dropSelf(ModBlocks.SOLIDIFICATION_CHAMBER);
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
                .setRolls(ConstantValue.exactly(1))
                .add(
                        LootItem.lootTableItem(block)
                        .when(HAS_SILK_TOUCH)
                        .apply(
                                CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                .copy("Bees", "BlockEntityTag.Bees")
                                .copy("Tier", "BlockEntityTag.Tier")
                                .copy("TierModifier", "BlockEntityTag.TierModifier")
                        )
                        .apply(
                            CopyBlockState.copyState(block).copy(BeehiveBlock.HONEY_LEVEL)
                        )
                        .otherwise(
                                LootItem.lootTableItem(block).apply(
                                        CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Tier", "BlockEntityTag.Tier").copy("TierModifier", "BlockEntityTag.TierModifier")
                            )
                        )
                );
    }
}
