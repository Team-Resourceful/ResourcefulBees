package com.teamresourceful.resourcefulbees.datagen.bases;

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class BaseBlockLootTable extends BlockLootTables {

    protected static final ILootCondition.IBuilder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));

    private final Set<Block> knownBlocks = new HashSet<>();

    @Override
    protected void add(@NotNull Block block, LootTable.@NotNull Builder builder) {
        super.add(block, builder);
        knownBlocks.add(block);
    }

    protected void add(RegistryObject<Block> registryObject, LootTable.@NotNull Builder builder){
        this.add(registryObject.get(), builder);
    }

    protected void add(RegistryObject<Block> registryObject, Function<Block, LootTable.Builder> factory){
        this.add(registryObject.get(), factory);
    }

    protected void dropSelf(RegistryObject<Block> registryObject) {
        this.dropSelf(registryObject.get());
    }

    @Override
    protected final @NotNull Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

}
