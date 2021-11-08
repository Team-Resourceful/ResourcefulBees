package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ModTags {

    private ModTags() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static class Items {
        public static final ITag.INamedTag<Item> HONEYCOMB_BLOCK = createItemTag(ResourcefulBees.MOD_ID, "resourceful_honeycomb_block");
        public static final ITag.INamedTag<Item> HONEYCOMB = createItemTag(ResourcefulBees.MOD_ID, "resourceful_honeycomb");
        public static final ITag.INamedTag<Item> WAX = createItemTag(ForgeVersion.MOD_ID, "wax");
        public static final ITag.INamedTag<Item> WAX_BLOCK = createItemTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
        public static final ITag.INamedTag<Item> SHEARS = createItemTag(ForgeVersion.MOD_ID, "shears");
        public static final ITag.INamedTag<Item> BEEHIVES = createItemTag("minecraft", "beehives");
        public static final ITag.INamedTag<Item> MUSHROOM = createItemTag(ForgeVersion.MOD_ID, "mushrooms");
        public static final ITag.INamedTag<Item> HONEY_BOTTLES = createItemTag(ForgeVersion.MOD_ID, "honey_bottle");

        private static net.minecraftforge.common.Tags.IOptionalNamedTag<Item> createItemTag(String mod, String path) {
            return ItemTags.createOptional(new ResourceLocation(mod, path));
        }
    }

    public static class Blocks {
        public static final ITag.INamedTag<Block> HONEYCOMB = createBlockTag(ResourcefulBees.MOD_ID, "resourceful_honeycomb_block");
        public static final ITag.INamedTag<Block> WAX = createBlockTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
        public static final ITag.INamedTag<Block> MUSHROOM = createBlockTag(ForgeVersion.MOD_ID, "mushrooms");

        private static net.minecraftforge.common.Tags.IOptionalNamedTag<Block> createBlockTag(String mod, String path) {
            return BlockTags.createOptional(new ResourceLocation(mod, path));
        }
    }

    public static class Fluids {
        public static final ITag.INamedTag<Fluid> HONEY = FluidTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "honey"));
    }
}
