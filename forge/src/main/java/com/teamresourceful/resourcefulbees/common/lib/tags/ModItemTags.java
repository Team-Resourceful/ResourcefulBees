package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.versions.forge.ForgeVersion;

public final class ModItemTags {
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
    public static final TagKey<Item> HONEY_BUCKETS = createItemTag(ForgeVersion.MOD_ID, "buckets/honey");

    public static final TagKey<Item> HEAT_SOURCES = createItemTag(ForgeVersion.MOD_ID, "heat_sources");

    private ModItemTags() {
        throw new UtilityClassError();
    }

    private static TagKey<Item> createItemTag(String mod, String path) {
        return ItemTags.create(new ResourceLocation(mod, path));
    }
}
