package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeBlocks;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
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
        tag(ModBlockTags.WAX).add(ModBlocks.WAX_BLOCK.get());
        tag(ModBlockTags.MUSHROOM).add(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        //noinspection unchecked
        tag(Tags.Blocks.STORAGE_BLOCKS).addTags(ModBlockTags.WAX);
        IntrinsicTagAppender<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        ModBlocks.HIVES.getEntries().stream().map(RegistryEntry::get).forEach(hiveBuilder::add);
        ModBlocks.APIARIES.getEntries().stream().map(RegistryEntry::get).forEach(hiveBuilder::add);

        IntrinsicTagAppender<Block> axeTagBuilder = tag(BlockTags.MINEABLE_WITH_AXE);
        ModBlocks.APIARIES.getEntries().stream().map(RegistryEntry::get).forEach(axeTagBuilder::add);
        axeTagBuilder.add(ModBlocks.BEEHOUSE_TOP.get(), ModBlocks.BREEDER_BLOCK.get(), ModBlocks.FLOW_HIVE.get());

        IntrinsicTagAppender<Block> pickaxeTagBuilder = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        ModBlocks.CENTRIFUGE_BLOCKS.getEntries().stream().map(RegistryEntry::get).forEach(pickaxeTagBuilder::add);

        pickaxeTagBuilder.add(
                ModBlocks.HONEY_GENERATOR.get(),
                ModBlocks.ENDER_BEECON.get(),
                ModBlocks.SOLIDIFICATION_CHAMBER.get(),
                ModBlocks.HONEY_POT.get()
        );

        IntrinsicTagAppender<Block> centrifugeTagBuilder = tag(ModBlockTags.CENTRIFUGE_PICKABLE);
        CentrifugeBlocks.CENTRIFUGE_BLOCKS.getEntries().stream().map(RegistryEntry::get).forEach(centrifugeTagBuilder::add);

        pickaxeTagBuilder.addOptionalTag(ModBlockTags.CENTRIFUGE_PICKABLE.location());

        tag(ModBlockTags.HEAT_SOURCES).add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.LAVA, Blocks.LAVA_CAULDRON,
                Blocks.MAGMA_BLOCK, Blocks.LANTERN, Blocks.SEA_LANTERN, Blocks.SOUL_LANTERN, Blocks.JACK_O_LANTERN);
        tag(ModBlockTags.HEAT_SOURCES).addTags(BlockTags.CAMPFIRES, BlockTags.FIRE, BlockTags.CANDLES);

        tag(ModBlockTags.NEST_PLACEABLE_ON)
                .addTags(BlockTags.LOGS, BlockTags.PLANKS, BlockTags.SAND, BlockTags.LEAVES, BlockTags.DIRT, BlockTags.ICE, BlockTags.SNOW)
                .addTags(BlockTags.STONE_BRICKS, BlockTags.BASE_STONE_OVERWORLD, BlockTags.BASE_STONE_NETHER)
                .addTags(ModBlockTags.COBBLESTONE, ModBlockTags.GLASS, ModBlockTags.GRAVEL, ModBlockTags.OBSIDIAN, ModBlockTags.ORES)
                .add(Blocks.SOUL_SAND, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.WATER, Blocks.LAVA, Blocks.CLAY)
                .add(Blocks.END_STONE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE)
                .add(Blocks.SOUL_SOIL, Blocks.POLISHED_BASALT, Blocks.GLOWSTONE, Blocks.PACKED_MUD, Blocks.MUD_BRICKS, Blocks.MAGMA_BLOCK);

        tag(BlockTags.WOODEN_FENCES).add(ModBlocks.WAXED_FENCE.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.WAXED_TRAPDOOR.get());
        tag(BlockTags.WOODEN_SLABS).add(ModBlocks.WAXED_SLAB.get());
        tag(BlockTags.WOODEN_STAIRS).add(ModBlocks.WAXED_STAIRS.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.WAXED_PRESSURE_PLATE.get());
        tag(BlockTags.WOODEN_DOORS).add(ModBlocks.WAXED_DOOR.get());
        tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.WAXED_BUTTON.get());
        tag(BlockTags.CEILING_HANGING_SIGNS).add(ModBlocks.WAXED_HANGING_SIGN.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(ModBlocks.WAXED_HANGING_SIGN.get());
        tag(BlockTags.ALL_HANGING_SIGNS).add(ModBlocks.WAXED_HANGING_SIGN.get());
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Block Tag Provider";
    }
}
