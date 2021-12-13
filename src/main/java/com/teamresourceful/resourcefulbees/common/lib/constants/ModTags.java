package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ModTags {

    private ModTags() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static class Items {
        public static final Tag.Named<Item> HONEYCOMB_BLOCK = createItemTag(ResourcefulBees.MOD_ID, "resourceful_honeycomb_block");
        public static final Tag.Named<Item> HONEYCOMB = createItemTag(ResourcefulBees.MOD_ID, "resourceful_honeycomb");
        public static final Tag.Named<Item> WAX = createItemTag(ForgeVersion.MOD_ID, "wax");
        public static final Tag.Named<Item> WAX_BLOCK = createItemTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
        public static final Tag.Named<Item> SHEARS = createItemTag(ForgeVersion.MOD_ID, "shears");
        public static final Tag.Named<Item> BEEHIVES = createItemTag("minecraft", "beehives");

        public static final Tag.Named<Item> T0_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t0_nests");
        public static final Tag.Named<Item> T1_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t1_nests");
        public static final Tag.Named<Item> T2_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t2_nests");
        public static final Tag.Named<Item> T3_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t3_nests");

        public static final Tag.Named<Item> MUSHROOM = createItemTag(ForgeVersion.MOD_ID, "mushrooms");
        public static final Tag.Named<Item> HONEY_BOTTLES = createItemTag(ForgeVersion.MOD_ID, "honey_bottle");

        private static net.minecraftforge.common.Tags.IOptionalNamedTag<Item> createItemTag(String mod, String path) {
            return ItemTags.createOptional(new ResourceLocation(mod, path));
        }
    }

    public static class Blocks {
        public static final Tag.Named<Block> HONEYCOMB = createBlockTag(ResourcefulBees.MOD_ID, "resourceful_honeycomb_block");
        public static final Tag.Named<Block> WAX = createBlockTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
        public static final Tag.Named<Block> MUSHROOM = createBlockTag(ForgeVersion.MOD_ID, "mushrooms");

        private static net.minecraftforge.common.Tags.IOptionalNamedTag<Block> createBlockTag(String mod, String path) {
            return BlockTags.createOptional(new ResourceLocation(mod, path));
        }
    }

    public static class Fluids {
        public static final Tag.Named<Fluid> HONEY = FluidTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "honey"));
    }
}
