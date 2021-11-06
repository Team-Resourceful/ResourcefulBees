package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
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
        tag(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS).addTags(ModTags.Blocks.WAX);
        Builder<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        ModBlocks.NEST_BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(hiveBuilder::add);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Block Tag Provider";
    }
}
