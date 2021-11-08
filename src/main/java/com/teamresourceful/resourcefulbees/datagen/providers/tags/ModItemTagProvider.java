package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, ResourcefulBees.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.Items.HONEYCOMB).add(Items.HONEYCOMB);
        tag(ModTags.Items.HONEYCOMB_BLOCK).add(Items.HONEYCOMB_BLOCK);
        Builder<Item> hiveBuilder = tag(ModTags.Items.BEEHIVES).add(Items.BEEHIVE).add(Items.BEE_NEST);
        ModItems.NESTS_ITEMS.getEntries().stream().map(RegistryObject::get).forEach(hiveBuilder::add);
        tag(ItemTags.SMALL_FLOWERS).add(ModItems.GOLD_FLOWER_ITEM.get());
        tag(ModTags.Items.WAX).add(ModItems.WAX.get());
        tag(ModTags.Items.WAX_BLOCK).add(ModItems.WAX_BLOCK_ITEM.get());
        //noinspection unchecked
        tag(net.minecraftforge.common.Tags.Items.STORAGE_BLOCKS).addTags(ModTags.Items.WAX_BLOCK);
        tag(ModTags.Items.SHEARS).add(Items.SHEARS);
        tag(ModTags.Items.MUSHROOM).add(Items.RED_MUSHROOM).add(Items.BROWN_MUSHROOM);
        tag(ModTags.Items.HONEY_BOTTLES).add(Items.HONEY_BOTTLE);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Item Tag Provider";
    }
}
