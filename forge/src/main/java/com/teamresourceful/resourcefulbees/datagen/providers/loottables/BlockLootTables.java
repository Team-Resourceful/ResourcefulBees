package com.teamresourceful.resourcefulbees.datagen.providers.loottables;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseBlockLootTable;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.Supplier;

public class BlockLootTables extends BaseBlockLootTable {

    private static final LootItemCondition.Builder WHEN_PLAYER_SHIFTING = LootItemEntityPropertyCondition.hasProperties(
        LootContext.EntityTarget.THIS,
        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setCrouching(true).build()).build()
    );

    @Override
    protected void addTables() {
        //region Misc
        dropSelf(ModBlocks.WAX_BLOCK);
        dropSelf(ModBlocks.GOLD_FLOWER);
        //endregion
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.HIVES.getEntries().forEach(this::addNest);
        //region Apiary
        dropSelf(ModBlocks.T1_APIARY_BLOCK);
        dropSelf(ModBlocks.T2_APIARY_BLOCK);
        dropSelf(ModBlocks.T3_APIARY_BLOCK);
        dropSelf(ModBlocks.T4_APIARY_BLOCK);
        dropSelf(ModBlocks.BREEDER_BLOCK);
        //endregion
        //region Machines
        dropSelf(ModBlocks.HONEY_GENERATOR);
        dropSelf(ModBlocks.SOLIDIFICATION_CHAMBER);
        dropSelf(ModBlocks.ENDER_BEECON);
        dropSelf(ModBlocks.HONEY_POT);
        //endregion

        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_MACHINE_BLOCK);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_PLANKS);
        BlockLoot.createDoorTable(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_DOOR.get());
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_BUTTON);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_FENCE);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_FENCE_GATE);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_PRESSURE_PLATE);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_SLAB);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_STAIRS);
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_TRAPDOOR);
        dropOther(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_SIGN.get(), ModItems.WAXED_SIGN.get());
        dropOther(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_WALL_SIGN.get(), ModItems.WAXED_SIGN.get());
        dropSelf(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.TRIMMED_WAXED_PLANKS);

        ModBlocks.CENTRIFUGE_BLOCKS.getEntries().forEach(this::dropSelf);
        addBeeBox(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BEE_BOX, false);
        addBeeBox(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BEE_BOX_TEMP, true);
    }

    private void addBeeBox(Supplier<Block> box, boolean temp) {
        Block block = box.get();

        LootPoolEntryContainer.Builder<?> drop = LootItem.lootTableItem(block)
            .when(WHEN_PLAYER_SHIFTING)
            .apply(
                CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                        .copy("Bees", "BlockEntityTag.Bees")
                        .copy("DisplayNames", "BlockEntityTag.DisplayNames")
            );

        if (!temp) {
            drop = drop.otherwise(LootItem.lootTableItem(block));
        }

        add(block, LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(drop)
        ));
    }

    private void addNest(RegistryEntry<Block> nest) {
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
                        LootItem.lootTableItem(block)
                        .apply(
                            CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                            .copy("Tier", "BlockEntityTag.Tier")
                            .copy("TierModifier", "BlockEntityTag.TierModifier")
                        )
                    )
                );
    }
}
