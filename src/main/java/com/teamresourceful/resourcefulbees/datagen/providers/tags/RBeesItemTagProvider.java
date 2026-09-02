package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModItemTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RBeesItemTagProvider extends ItemTagsProvider {

    public RBeesItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ModConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addCommonTags();
        addHiveTags();
        addWaxedWoodTags();
    }

    private void addCommonTags() {
        tag(ModItemTags.HONEYCOMBS).add(Items.HONEYCOMB.builtInRegistryHolder().getKey());

        tag(ModItemTags.HONEYCOMB_STORAGE_BLOCKS).add(Items.HONEYCOMB_BLOCK.builtInRegistryHolder().getKey());

        tag(ModItemTags.HONEY_BUCKETS).add(ModItems.HONEY_BUCKET.holder().getKey());

        tag(ModItemTags.HONEY_BLOCKS).add(Items.HONEY_BLOCK.builtInRegistryHolder().getKey());

        tag(ModItemTags.HONEY_BOTTLES).add(Items.HONEY_BOTTLE.builtInRegistryHolder().getKey());

        tag(ModItemTags.WAX).add(ModItems.WAX.holder().getKey());

        tag(ModItemTags.WAX_STORAGE_BLOCKS).add(ModItems.WAX_BLOCK_ITEM.holder().getKey());

        tag(Tags.Items.STORAGE_BLOCKS).addTag(ModItemTags.WAX);

        tag(ModItemTags.HEAT_SOURCES)
                .add(
                        Items.TORCH.builtInRegistryHolder().getKey(),
                        Items.SOUL_TORCH.builtInRegistryHolder().getKey(),
                        Items.LAVA_BUCKET.builtInRegistryHolder().getKey(),
                        Items.CAMPFIRE.builtInRegistryHolder().getKey(),
                        Items.SOUL_CAMPFIRE.builtInRegistryHolder().getKey(),
                        Items.MAGMA_BLOCK.builtInRegistryHolder().getKey(),
                        Items.LANTERN.builtInRegistryHolder().getKey(),
                        Items.SEA_LANTERN.builtInRegistryHolder().getKey(),
                        Items.SOUL_LANTERN.builtInRegistryHolder().getKey(),
                        Items.JACK_O_LANTERN.builtInRegistryHolder().getKey()
                )
                .addTag(ItemTags.CANDLES);

        tag(Tags.Items.FLOWERS_SMALL).add(ModItems.GOLD_FLOWER_ITEM.holder().getKey());
    }

    private void addHiveTags() {
        TagAppender<Item> beehives = tag(ModItemTags.BEEHIVES)
                .add(
                        Items.BEEHIVE.builtInRegistryHolder().getKey(),
                        Items.BEE_NEST.builtInRegistryHolder().getKey()
                );

        TagAppender<Item> t1Nests = tag(ModItemTags.T1_NESTS);
        TagAppender<Item> t2Nests = tag(ModItemTags.T2_NESTS);
        TagAppender<Item> t3Nests = tag(ModItemTags.T3_NESTS);
        TagAppender<Item> t4Nests = tag(ModItemTags.T4_NESTS);

        ModItems.T1_NEST_ITEMS.getEntries().forEach(entry -> addToTags((HolderRegistryEntry<Item>) entry, beehives, t1Nests));
        ModItems.T2_NEST_ITEMS.getEntries().forEach(entry -> addToTags((HolderRegistryEntry<Item>) entry, beehives, t2Nests));
        ModItems.T3_NEST_ITEMS.getEntries().forEach(entry -> addToTags((HolderRegistryEntry<Item>) entry, beehives, t3Nests));
        ModItems.T4_NEST_ITEMS.getEntries().forEach(entry -> addToTags((HolderRegistryEntry<Item>) entry, beehives, t4Nests));
    }

    private void addWaxedWoodTags() {
        tag(ItemTags.WOODEN_DOORS).add(ModItems.WAXED_DOOR.holder().getKey());
        tag(ItemTags.WOODEN_BUTTONS).add(ModItems.WAXED_BUTTON.holder().getKey());
        tag(ItemTags.WOODEN_FENCES).add(ModItems.WAXED_FENCE.holder().getKey());
        tag(ItemTags.WOODEN_SLABS).add(ModItems.WAXED_SLAB.holder().getKey());
        tag(ItemTags.WOODEN_STAIRS).add(ModItems.WAXED_STAIRS.holder().getKey());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(ModItems.WAXED_PRESSURE_PLATE.holder().getKey());
        tag(ItemTags.WOODEN_TRAPDOORS).add(ModItems.WAXED_TRAPDOOR.holder().getKey());
        tag(ItemTags.HANGING_SIGNS).add(ModItems.WAXED_HANGING_SIGN.holder().getKey());
        tag(ItemTags.SIGNS).add(ModItems.WAXED_SIGN.holder().getKey());
    }

    @SafeVarargs
    private static void addToTags(
            HolderRegistryEntry<Item> entry,
            TagAppender<Item>... appenders
    ) {
        ResourceKey<Item> key = entry.holder().getKey();

        for (TagAppender<Item> appender : appenders) {
            appender.add(key);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Item Tags";
    }
}