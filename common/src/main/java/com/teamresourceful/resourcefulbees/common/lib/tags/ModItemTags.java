package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    public static final TagKey<Item> HONEYCOMB_BLOCK = createItemTag("forge", "storage_blocks/honeycombs");
    public static final TagKey<Item> HONEYCOMB = createItemTag("forge", "honeycombs");
    public static final TagKey<Item> WAX = createItemTag("forge", "wax");
    public static final TagKey<Item> WAX_BLOCK = createItemTag("forge", "storage_blocks/wax");
    public static final TagKey<Item> SHEARS = createItemTag("forge", "shears");
    public static final TagKey<Item> BEEHIVES = createItemTag("minecraft", "beehives");

    public static final TagKey<Item> T0_NESTS = createItemTag(ModConstants.MOD_ID, "t0_nests");
    public static final TagKey<Item> T1_NESTS = createItemTag(ModConstants.MOD_ID, "t1_nests");
    public static final TagKey<Item> T2_NESTS = createItemTag(ModConstants.MOD_ID, "t2_nests");
    public static final TagKey<Item> T3_NESTS = createItemTag(ModConstants.MOD_ID, "t3_nests");

    public static final TagKey<Item> MUSHROOM = createItemTag("forge", "mushrooms");
    public static final TagKey<Item> HONEY_BOTTLES = createItemTag("forge", "honey_bottles");
    public static final TagKey<Item> HONEY_BUCKETS = createItemTag("forge", "buckets/honey");
    public static final TagKey<Item> HONEY_BLOCKS = createItemTag("forge", "honey_blocks");

    public static final TagKey<Item> HEAT_SOURCES = createItemTag("forge", "heat_sources");

    private ModItemTags() {
        throw new UtilityClassError();
    }

    private static TagKey<Item> createItemTag(String mod, String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(mod, path));
    }
}
