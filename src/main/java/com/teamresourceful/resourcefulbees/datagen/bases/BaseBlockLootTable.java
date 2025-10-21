package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseBlockLootTable extends BlockLootSubProvider {

    protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    private final Set<Block> knownBlocks = new HashSet<>();

    protected BaseBlockLootTable() {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    protected void add(@NotNull Block block, LootTable.@NotNull Builder builder) {
        super.add(block, builder);
        knownBlocks.add(block);
    }

    protected void add(RegistryEntry<Block> registryObject, LootTable.@NotNull Builder builder){
        this.add(registryObject.get(), builder);
    }

    protected void add(RegistryEntry<Block> registryObject, Function<Block, LootTable.Builder> factory){
        this.add(registryObject.get(), factory);
    }

    protected void dropSelf(RegistryEntry<? extends Block> registryObject) {
        this.dropSelf(registryObject.get());
    }

    @Override
    protected final @NotNull Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

}
