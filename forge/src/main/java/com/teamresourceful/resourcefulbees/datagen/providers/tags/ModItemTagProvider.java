package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModItemTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), provider, blockTagsProvider, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "honeycombs"))).add(Items.HONEYCOMB);
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "storage_blocks/honeycombs"))).add(Items.HONEYCOMB_BLOCK);
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "buckets/honey"))).add(ModItems.HONEY_BUCKET.get());
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "honey_blocks"))).add(Items.HONEY_BLOCK);
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "honey_bottles"))).add(Items.HONEY_BOTTLE);
        IntrinsicTagAppender<Item> hiveBuilder = tag(ModItemTags.BEEHIVES).add(Items.BEEHIVE).add(Items.BEE_NEST);
        IntrinsicTagAppender<Item> t0Nests = tag(ModItemTags.T0_NESTS);
        IntrinsicTagAppender<Item> t1Nests = tag(ModItemTags.T1_NESTS);
        IntrinsicTagAppender<Item> t2Nests = tag(ModItemTags.T2_NESTS);
        IntrinsicTagAppender<Item> t3Nests = tag(ModItemTags.T3_NESTS);
        ModItems.T1_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t0Nests));
        ModItems.T2_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t1Nests));
        ModItems.T3_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t2Nests));
        ModItems.T4_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).forEach(item -> addToBuilders(item, hiveBuilder, t3Nests));
        tag(ItemTags.SMALL_FLOWERS).add(ModItems.GOLD_FLOWER_ITEM.get());
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "wax"))).add(ModItems.WAX.get());
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "storage_blocks/wax"))).add(ModItems.WAX_BLOCK_ITEM.get());
        tag(Tags.Items.STORAGE_BLOCKS).addTags(ModItemTags.WAX_BLOCK);
        tag(TagKey.create(Registries.ITEM, new ResourceLocation(ForgeVersion.MOD_ID, "heat_sources")))
            .add(Items.TORCH, Items.SOUL_TORCH, Items.LAVA_BUCKET, Items.CAMPFIRE, Items.SOUL_CAMPFIRE, Items.MAGMA_BLOCK)
            .add(Items.LANTERN, Items.SEA_LANTERN, Items.SOUL_LANTERN, Items.JACK_O_LANTERN)
            .addTags(ItemTags.CANDLES);

        tag(ItemTags.WOODEN_DOORS).add(ModItems.WAXED_DOOR.get());
        tag(ItemTags.WOODEN_BUTTONS).add(ModItems.WAXED_BUTTON.get());
        tag(ItemTags.WOODEN_FENCES).add(ModItems.WAXED_DOOR.get());
        tag(ItemTags.WOODEN_SLABS).add(ModItems.WAXED_SLAB.get());
        tag(ItemTags.WOODEN_STAIRS).add(ModItems.WAXED_STAIRS.get());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(ModItems.WAXED_PRESSURE_PLATE.get());
        tag(ItemTags.WOODEN_TRAPDOORS).add(ModItems.WAXED_TRAPDOOR.get());
        tag(ItemTags.HANGING_SIGNS).add(ModItems.WAXED_HANGING_SIGN.get());
        tag(ItemTags.SIGNS).add(ModItems.WAXED_SIGN.get());
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
