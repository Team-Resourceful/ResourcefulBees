package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        tag(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS).addTags(ModTags.Blocks.WAX);
        TagAppender<Block> hiveBuilder = tag(BlockTags.BEEHIVES);
        ModBlocks.HIVES.getEntries().stream().map(RegistryObject::get).forEach(hiveBuilder::add);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Block Tag Provider";
    }
}
