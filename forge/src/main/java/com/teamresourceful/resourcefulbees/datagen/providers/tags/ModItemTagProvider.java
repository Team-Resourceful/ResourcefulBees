package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModItemTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), provider, blockTagsProvider, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModItemTags.HONEYCOMB).add(Items.HONEYCOMB);
        tag(ModItemTags.HONEYCOMB_BLOCK).add(Items.HONEYCOMB_BLOCK);
        tag(ModItemTags.HONEY_BUCKETS).add(ModItems.HONEY_FLUID_BUCKET.get());
        tag(ModItemTags.HONEY_BLOCKS).add(Items.HONEY_BLOCK);
        tag(ModItemTags.HONEY_BOTTLES).add(Items.HONEY_BOTTLE);
        IntrinsicTagAppender<Item> hiveBuilder = tag(ModItemTags.BEEHIVES).add(Items.BEEHIVE).add(Items.BEE_NEST);
        IntrinsicTagAppender<Item> t0Nests = tag(ModItemTags.T0_NESTS);
        IntrinsicTagAppender<Item> t1Nests = tag(ModItemTags.T1_NESTS);
        IntrinsicTagAppender<Item> t2Nests = tag(ModItemTags.T2_NESTS);
        IntrinsicTagAppender<Item> t3Nests = tag(ModItemTags.T3_NESTS);
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T1_NEST_ITEMS.getEntries().stream().map(com.teamresourceful.resourcefullib.common.registry.RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t0Nests));
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_NEST_ITEMS.getEntries().stream().map(com.teamresourceful.resourcefullib.common.registry.RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t1Nests));
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T3_NEST_ITEMS.getEntries().stream().map(com.teamresourceful.resourcefullib.common.registry.RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t2Nests));
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T4_NEST_ITEMS.getEntries().stream().map(com.teamresourceful.resourcefullib.common.registry.RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t3Nests));
        tag(ItemTags.SMALL_FLOWERS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.GOLD_FLOWER_ITEM.get());
        tag(ModItemTags.WAX).add(ModItems.WAX.get());
        tag(ModItemTags.WAX_BLOCK).add(ModItems.WAX_BLOCK_ITEM.get());
        tag(Tags.Items.STORAGE_BLOCKS).addTags(ModItemTags.WAX_BLOCK);
        tag(ModItemTags.SHEARS).add(Items.SHEARS);
        tag(ModItemTags.MUSHROOM).add(Items.RED_MUSHROOM).add(Items.BROWN_MUSHROOM);
        tag(ModItemTags.HEAT_SOURCES)
            .add(Items.TORCH, Items.SOUL_TORCH, Items.LAVA_BUCKET, Items.CAMPFIRE, Items.SOUL_CAMPFIRE, Items.MAGMA_BLOCK)
            .add(Items.LANTERN, Items.SEA_LANTERN, Items.SOUL_LANTERN, Items.JACK_O_LANTERN)
            .addTags(ItemTags.CANDLES);

        tag(ItemTags.WOODEN_DOORS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_DOOR.get());
        tag(ItemTags.WOODEN_BUTTONS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_BUTTON.get());
        tag(ItemTags.WOODEN_FENCES).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_DOOR.get());
        tag(ItemTags.WOODEN_SLABS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_SLAB.get());
        tag(ItemTags.WOODEN_STAIRS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_STAIRS.get());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_PRESSURE_PLATE.get());
        tag(ItemTags.WOODEN_TRAPDOORS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_TRAPDOOR.get());
    }

    @SafeVarargs
    private void addToBuilders(Item item, IntrinsicTagAppender<Item>... appenders) {
        for (IntrinsicTagAppender<Item> appender : appenders) {
            appender.add(item);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Item Tag Provider";
    }
}
