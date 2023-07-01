package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeBlocks;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), provider, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModBlockTags.HONEYCOMB).add(Blocks.HONEYCOMB_BLOCK);
        tag(ModBlockTags.WAX).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAX_BLOCK.get());
        tag(ModBlockTags.MUSHROOM).add(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        //noinspection unchecked
        tag(Tags.Blocks.STORAGE_BLOCKS).addTags(ModBlockTags.WAX);
        IntrinsicTagAppender<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.HIVES.getEntries().stream().map(RegistryEntry::get).forEach(hiveBuilder::add);
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.APIARIES.getEntries().stream().map(RegistryEntry::get).forEach(hiveBuilder::add);

        IntrinsicTagAppender<Block> axeTagBuilder = tag(BlockTags.MINEABLE_WITH_AXE);
        com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.APIARIES.getEntries().stream().map(RegistryEntry::get).forEach(axeTagBuilder::add);
        axeTagBuilder.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BEEHOUSE_TOP.get(), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.BREEDER_BLOCK.get(), com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.FLOW_HIVE.get());

        IntrinsicTagAppender<Block> pickaxeTagBuilder = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        ModBlocks.CENTRIFUGE_BLOCKS.getEntries().stream().map(RegistryEntry::get).forEach(pickaxeTagBuilder::add);
        CentrifugeBlocks.CENTRIFUGE_BLOCKS.getEntries().stream().map(RegistryEntry::get).forEach(pickaxeTagBuilder::add);
        pickaxeTagBuilder.add(ModBlocks.HONEY_GENERATOR.get(), ModBlocks.ENDER_BEECON.get(), ModBlocks.SOLIDIFICATION_CHAMBER.get(), ModBlocks.HONEY_POT.get());

        tag(ModBlockTags.HEAT_SOURCES).add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.LAVA, Blocks.LAVA_CAULDRON,
                Blocks.MAGMA_BLOCK, Blocks.LANTERN, Blocks.SEA_LANTERN, Blocks.SOUL_LANTERN, Blocks.JACK_O_LANTERN);
        tag(ModBlockTags.HEAT_SOURCES).addTags(BlockTags.CAMPFIRES, BlockTags.FIRE, BlockTags.CANDLES);

        tag(ModBlockTags.NEST_PLACEABLE_ON)
                .addTags(BlockTags.LOGS, BlockTags.PLANKS, BlockTags.SAND, BlockTags.LEAVES, BlockTags.DIRT, BlockTags.ICE, BlockTags.SNOW)
                .addTags(BlockTags.STONE_BRICKS, Tags.Blocks.SANDSTONE, Tags.Blocks.STONE, Tags.Blocks.NETHERRACK, Tags.Blocks.END_STONES)
                .addTags(Tags.Blocks.COBBLESTONE, Tags.Blocks.GLASS, Tags.Blocks.GRAVEL, Tags.Blocks.OBSIDIAN, Tags.Blocks.ORES)
                .add(Blocks.SOUL_SAND, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.WATER, Blocks.LAVA, Blocks.CLAY)
                .add(Blocks.SOUL_SOIL, Blocks.POLISHED_BASALT, Blocks.GLOWSTONE, Blocks.PACKED_MUD, Blocks.MUD_BRICKS, Blocks.MAGMA_BLOCK);

        tag(BlockTags.WOODEN_FENCES).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_FENCE.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_TRAPDOOR.get());
        tag(BlockTags.WOODEN_SLABS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_SLAB.get());
        tag(BlockTags.WOODEN_STAIRS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_STAIRS.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_PRESSURE_PLATE.get());
        tag(BlockTags.WOODEN_DOORS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_DOOR.get());
        tag(BlockTags.WOODEN_BUTTONS).add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.WAXED_BUTTON.get());
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Block Tag Provider";
    }
}
