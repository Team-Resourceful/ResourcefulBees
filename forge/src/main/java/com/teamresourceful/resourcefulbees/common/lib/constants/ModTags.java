package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.versions.forge.ForgeVersion;

public final class ModTags {

    private ModTags() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final class Items {
        public static final TagKey<Item> HONEYCOMB_BLOCK = createItemTag(ForgeVersion.MOD_ID, "storage_blocks/honeycombs");
        public static final TagKey<Item> HONEYCOMB = createItemTag(ForgeVersion.MOD_ID, "honeycombs");
        public static final TagKey<Item> WAX = createItemTag(ForgeVersion.MOD_ID, "wax");
        public static final TagKey<Item> WAX_BLOCK = createItemTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
        public static final TagKey<Item> SHEARS = createItemTag(ForgeVersion.MOD_ID, "shears");
        public static final TagKey<Item> BEEHIVES = createItemTag("minecraft", "beehives");

        public static final TagKey<Item> T0_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t0_nests");
        public static final TagKey<Item> T1_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t1_nests");
        public static final TagKey<Item> T2_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t2_nests");
        public static final TagKey<Item> T3_NESTS = createItemTag(ResourcefulBees.MOD_ID, "t3_nests");

        public static final TagKey<Item> MUSHROOM = createItemTag(ForgeVersion.MOD_ID, "mushrooms");
        public static final TagKey<Item> HONEY_BOTTLES = createItemTag(ForgeVersion.MOD_ID, "honey_bottles");

        public static final TagKey<Item> HEAT_SOURCES = createItemTag(ForgeVersion.MOD_ID, "heat_sources");

        private Items() {
            throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
        }

        private static TagKey<Item> createItemTag(String mod, String path) {
            return ItemTags.create(new ResourceLocation(mod, path));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> HONEYCOMB = createBlockTag(ForgeVersion.MOD_ID, "storage_blocks/honeycombs");
        public static final TagKey<Block> WAX = createBlockTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
        public static final TagKey<Block> MUSHROOM = createBlockTag(ForgeVersion.MOD_ID, "mushrooms");

        public static final TagKey<Block> HEAT_SOURCES = createBlockTag(ForgeVersion.MOD_ID, "heat_sources");
        public static final TagKey<Block> NEST_PLACEABLE_ON = createBlockTag(ResourcefulBees.MOD_ID, "nest_placeable_on");

        private Blocks() {
            throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
        }

        private static TagKey<Block> createBlockTag(String mod, String path) {
            return BlockTags.create(new ResourceLocation(mod, path));
        }
    }

    public static final class Fluids {
        public static final TagKey<Fluid> HONEY = FluidTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "honey"));

        private Fluids() {
            throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
        }
    }
}
