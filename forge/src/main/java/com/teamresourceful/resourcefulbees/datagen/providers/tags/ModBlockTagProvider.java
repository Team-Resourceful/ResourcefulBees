package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModBlockTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags() {
        tag(ModBlockTags.HONEYCOMB).add(Blocks.HONEYCOMB_BLOCK);
        tag(ModBlockTags.WAX).add(ModBlocks.WAX_BLOCK.get());
        tag(ModBlockTags.MUSHROOM).add(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        //noinspection unchecked
        tag(Tags.Blocks.STORAGE_BLOCKS).addTags(ModBlockTags.WAX);
        TagAppender<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        ModBlocks.HIVES.getEntries().stream().map(RegistryEntry::get).forEach(hiveBuilder::add);
        ModBlocks.APIARIES.getEntries().stream().map(RegistryEntry::get).forEach(hiveBuilder::add);

        TagAppender<Block> axeTagBuilder = tag(BlockTags.MINEABLE_WITH_AXE);
        ModBlocks.APIARIES.getEntries().stream().map(RegistryEntry::get).forEach(axeTagBuilder::add);
        axeTagBuilder.add(ModBlocks.BEEHOUSE_TOP.get(), ModBlocks.BREEDER_BLOCK.get(), ModBlocks.FLOW_HIVE.get());

        TagAppender<Block> pickaxeTagBuilder = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        ModBlocks.CENTRIFUGE_BLOCKS.getEntries().stream().map(RegistryEntry::get).forEach(pickaxeTagBuilder::add);
        pickaxeTagBuilder.add(ModBlocks.HONEY_GENERATOR.get(), ModBlocks.ENDER_BEECON.get(), ModBlocks.SOLIDIFICATION_CHAMBER.get(), ModBlocks.HONEY_POT.get());

        tag(ModBlockTags.HEAT_SOURCES).add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.LAVA, Blocks.LAVA_CAULDRON,
                Blocks.MAGMA_BLOCK, Blocks.LANTERN, Blocks.SEA_LANTERN, Blocks.SOUL_LANTERN, Blocks.JACK_O_LANTERN);
        tag(ModBlockTags.HEAT_SOURCES).addTags(BlockTags.CAMPFIRES, BlockTags.FIRE, BlockTags.CANDLES);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Block Tag Provider";
    }
}
