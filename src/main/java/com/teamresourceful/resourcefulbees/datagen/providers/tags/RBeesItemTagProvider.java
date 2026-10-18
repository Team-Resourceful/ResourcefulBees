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
        tag(ItemTags.DECORATED_POT_SHERDS).add(ModItems.HONEYCOMB_POTTERY_SHERD.holder().key());
        tag(ItemTags.DECORATED_POT_SHERDS).add(ModItems.BEE_POTTERY_SHERD.holder().key());
    }

    private void addCommonTags() {
        tag(ModItemTags.HONEYCOMBS).add(Items.HONEYCOMB.builtInRegistryHolder().key());

        tag(ModItemTags.HONEYCOMB_STORAGE_BLOCKS).add(Items.HONEYCOMB_BLOCK.builtInRegistryHolder().key());

        tag(ModItemTags.HONEY_BUCKETS).add(ModItems.HONEY_BUCKET.holder().key());

        tag(ModItemTags.HONEY_BLOCKS).add(Items.HONEY_BLOCK.builtInRegistryHolder().key());

        tag(ModItemTags.HONEY_BOTTLES).add(Items.HONEY_BOTTLE.builtInRegistryHolder().key());

        tag(ModItemTags.WAX).add(ModItems.WAX.holder().key());

        tag(ModItemTags.WAX_STORAGE_BLOCKS).add(ModItems.WAX_BLOCK_ITEM.holder().key());

        tag(Tags.Items.STORAGE_BLOCKS).addTag(ModItemTags.WAX);

        tag(ModItemTags.HEAT_SOURCES)
                .add(
                        Items.TORCH.builtInRegistryHolder().key(),
                        Items.SOUL_TORCH.builtInRegistryHolder().key(),
                        Items.LAVA_BUCKET.builtInRegistryHolder().key(),
                        Items.CAMPFIRE.builtInRegistryHolder().key(),
                        Items.SOUL_CAMPFIRE.builtInRegistryHolder().key(),
                        Items.MAGMA_BLOCK.builtInRegistryHolder().key(),
                        Items.LANTERN.builtInRegistryHolder().key(),
                        Items.SEA_LANTERN.builtInRegistryHolder().key(),
                        Items.SOUL_LANTERN.builtInRegistryHolder().key(),
                        Items.JACK_O_LANTERN.builtInRegistryHolder().key()
                )
                .addTag(ItemTags.CANDLES);

        tag(Tags.Items.FLOWERS_TALL).add(ModItems.GOLD_ROSE_BUSH.holder().key());
    }

    private void addHiveTags() {
        TagAppender<Item> beehives = tag(ModItemTags.BEEHIVES)
                .add(
                        Items.BEEHIVE.builtInRegistryHolder().key(),
                        Items.BEE_NEST.builtInRegistryHolder().key()
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
        tag(ModItemTags.WAXED_TOOL_MATERIALS).addTags(ModItemTags.WAX);
        tag(ItemTags.WOODEN_DOORS).add(ModItems.WAXED_DOOR.holder().key());
        tag(ItemTags.WOODEN_BUTTONS).add(ModItems.WAXED_BUTTON.holder().key());
        tag(ItemTags.WOODEN_FENCES).add(ModItems.WAXED_FENCE.holder().key());
        tag(ItemTags.WOODEN_SLABS).add(ModItems.WAXED_SLAB.holder().key());
        tag(ItemTags.WOODEN_STAIRS).add(ModItems.WAXED_STAIRS.holder().key());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(ModItems.WAXED_PRESSURE_PLATE.holder().key());
        tag(ItemTags.WOODEN_TRAPDOORS).add(ModItems.WAXED_TRAPDOOR.holder().key());
        tag(ItemTags.HANGING_SIGNS).add(ModItems.WAXED_HANGING_SIGN.holder().key());
        tag(ItemTags.SIGNS).add(ModItems.WAXED_SIGN.holder().key());
    }

    @SafeVarargs
    private static void addToTags(
            HolderRegistryEntry<Item> entry,
            TagAppender<Item>... appenders
    ) {
        ResourceKey<Item> key = entry.holder().key();

        for (TagAppender<Item> appender : appenders) {
            appender.add(key);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Item Tags";
    }
}