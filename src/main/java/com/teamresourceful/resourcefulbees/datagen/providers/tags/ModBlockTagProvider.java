package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ResourcefulBees.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.Blocks.HONEYCOMB).add(Blocks.HONEYCOMB_BLOCK);
        tag(ModTags.Blocks.WAX).add(ModBlocks.WAX_BLOCK.get());
        tag(ModTags.Blocks.MUSHROOM).add(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        //noinspection unchecked
        tag(Tags.Blocks.STORAGE_BLOCKS).addTags(ModTags.Blocks.WAX);
        TagAppender<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        ModBlocks.HIVES.getEntries().stream().map(RegistryObject::get).forEach(hiveBuilder::add);
        ModBlocks.APIARIES.getEntries().stream().map(RegistryObject::get).forEach(hiveBuilder::add);

        TagAppender<Block> axeTagBuilder = tag(BlockTags.MINEABLE_WITH_AXE);
        ModBlocks.APIARIES.getEntries().stream().map(RegistryObject::get).forEach(axeTagBuilder::add);
        axeTagBuilder.add(ModBlocks.BEEHOUSE_TOP.get(), ModBlocks.BREEDER_BLOCK.get());

        TagAppender<Block> pickaxeTagBuilder = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        ModBlocks.CENTRIFUGE_BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(pickaxeTagBuilder::add);
        pickaxeTagBuilder.add(ModBlocks.HONEY_GENERATOR.get(), ModBlocks.ENDER_BEECON.get(), ModBlocks.SOLIDIFICATION_CHAMBER.get(), ModBlocks.HONEY_POT.get());
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Block Tag Provider";
    }
}
