package com.teamresourceful.resourcefulbees.datagen.providers;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RBeesBlockTagProvider extends BlockTagsProvider {

    public RBeesBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        tag(ModBlockTags.HONEYCOMB).add(blockKey(Blocks.HONEYCOMB_BLOCK));
        tag(ModBlockTags.WAX).add(blockKey(ModBlocks.WAX_BLOCK));
        tag(ModBlockTags.MUSHROOM).add(blockKey(Blocks.RED_MUSHROOM), blockKey(Blocks.BROWN_MUSHROOM));
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(ModBlockTags.WAX);

        TagAppender<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        ModBlocks.HIVES.getEntries()
                .stream()
                .map(RBeesBlockTagProvider::blockKey)
                .forEach(hiveBuilder::add);

        ModBlocks.APIARIES.getEntries()
                .stream()
                .map(RBeesBlockTagProvider::blockKey)
                .forEach(hiveBuilder::add);

        TagAppender<Block> axeTagBuilder =
                tag(BlockTags.MINEABLE_WITH_AXE);

        ModBlocks.APIARIES.getEntries()
                .stream()
                .map(RBeesBlockTagProvider::blockKey)
                .forEach(axeTagBuilder::add);

        axeTagBuilder.add(
                blockKey(ModBlocks.BEEHOUSE_TOP),
                blockKey(ModBlocks.BREEDER_BLOCK),
                blockKey(ModBlocks.FLOW_HIVE)
        );

        TagAppender<Block> pickaxeTagBuilder =
                tag(BlockTags.MINEABLE_WITH_PICKAXE);

        ModBlocks.CENTRIFUGE_BLOCKS.getEntries()
                .stream()
                .map(RBeesBlockTagProvider::blockKey)
                .forEach(pickaxeTagBuilder::add);

        pickaxeTagBuilder.add(
                blockKey(ModBlocks.HONEY_GENERATOR),
                blockKey(ModBlocks.ENDER_BEECON),
                blockKey(ModBlocks.SOLIDIFICATION_CHAMBER),
                blockKey(ModBlocks.HONEY_POT)
        );

        tag(ModBlockTags.CENTRIFUGE_PICKABLE);

        pickaxeTagBuilder.addOptionalTag(
                ModBlockTags.CENTRIFUGE_PICKABLE
        );

        tag(ModBlockTags.HEAT_SOURCES)
                .add(
                        blockKey(Blocks.TORCH),
                        blockKey(Blocks.SOUL_TORCH),
                        blockKey(Blocks.WALL_TORCH),
                        blockKey(Blocks.SOUL_WALL_TORCH),
                        blockKey(Blocks.LAVA),
                        blockKey(Blocks.LAVA_CAULDRON),
                        blockKey(Blocks.MAGMA_BLOCK),
                        blockKey(Blocks.LANTERN),
                        blockKey(Blocks.SEA_LANTERN),
                        blockKey(Blocks.SOUL_LANTERN),
                        blockKey(Blocks.JACK_O_LANTERN)
                )
                .addTags(
                        BlockTags.CAMPFIRES,
                        BlockTags.FIRE,
                        BlockTags.CANDLES
                );

        tag(ModBlockTags.GOLD_FLOWER_BONEMEALABLE).addTags(BlockTags.GRASS_BLOCKS);

        tag(ModBlockTags.NEST_PLACEABLE_ON)
                .addTags(
                        BlockTags.LOGS,
                        BlockTags.PLANKS,
                        BlockTags.SAND,
                        BlockTags.LEAVES,
                        BlockTags.GRASS_BLOCKS,
                        BlockTags.DIRT,
                        BlockTags.ICE,
                        BlockTags.SNOW,
                        BlockTags.STONE_BRICKS,
                        BlockTags.BASE_STONE_OVERWORLD,
                        BlockTags.BASE_STONE_NETHER
                        )
                .addTags(
                        Tags.Blocks.COBBLESTONES,
                        Tags.Blocks.GLASS_BLOCKS,
                        Tags.Blocks.GRAVELS,
                        Tags.Blocks.NATURAL_LOGS,
                        Tags.Blocks.NATURAL_WOODS,
                        Tags.Blocks.STONES,
                        Tags.Blocks.END_STONES,
                        Tags.Blocks.NETHERRACKS,
                        Tags.Blocks.SANDS,
                        Tags.Blocks.OBSIDIANS,
                        Tags.Blocks.ORES
                )
                .add(
                        blockKey(Blocks.SOUL_SAND),
                        blockKey(Blocks.BASALT),
                        blockKey(Blocks.BLACKSTONE),
                        blockKey(Blocks.POLISHED_BLACKSTONE),
                        blockKey(Blocks.WATER),
                        blockKey(Blocks.LAVA),
                        blockKey(Blocks.CLAY),
                        blockKey(Blocks.END_STONE),
                        blockKey(Blocks.SANDSTONE),
                        blockKey(Blocks.RED_SANDSTONE),
                        blockKey(Blocks.SOUL_SOIL),
                        blockKey(Blocks.POLISHED_BASALT),
                        blockKey(Blocks.GLOWSTONE),
                        blockKey(Blocks.PACKED_MUD),
                        blockKey(Blocks.MUD_BRICKS),
                        blockKey(Blocks.MAGMA_BLOCK)
                );

        tag(BlockTags.WOODEN_FENCES).add(blockKey(ModBlocks.WAXED_FENCE));
        tag(BlockTags.WOODEN_TRAPDOORS).add(blockKey(ModBlocks.WAXED_TRAPDOOR));
        tag(BlockTags.WOODEN_SLABS).add(blockKey(ModBlocks.WAXED_SLAB));
        tag(BlockTags.WOODEN_STAIRS).add(blockKey(ModBlocks.WAXED_STAIRS));
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(blockKey(ModBlocks.WAXED_PRESSURE_PLATE));
        tag(BlockTags.WOODEN_DOORS).add(blockKey(ModBlocks.WAXED_DOOR));
        tag(BlockTags.WOODEN_BUTTONS).add(blockKey(ModBlocks.WAXED_BUTTON));
        tag(BlockTags.CEILING_HANGING_SIGNS).add(blockKey(ModBlocks.WAXED_HANGING_SIGN));
        tag(BlockTags.WALL_HANGING_SIGNS).add(blockKey(ModBlocks.WAXED_HANGING_SIGN));
        tag(BlockTags.ALL_HANGING_SIGNS).add(blockKey(ModBlocks.WAXED_HANGING_SIGN));
    }

    private static ResourceKey<Block> blockKey(RegistryEntry<? extends Block> entry) {
        return ResourceKey.create(
                Registries.BLOCK,
                entry.getId()
        );
    }

    private static ResourceKey<Block> blockKey(Block block) {
        return BuiltInRegistries.BLOCK
                .getResourceKey(block)
                .orElseThrow(() -> new IllegalStateException("Block is not registered: " + block));
    }

    @Override
    public @NonNull String getName() {
        return "Resourceful Bees Block Tags";
    }
}