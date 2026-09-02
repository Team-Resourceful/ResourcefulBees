package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    public static final TagKey<Item> BEEHIVES = TagKey.create(Registries.ITEM, ModIdentifier.of("beehives"));
    public static final TagKey<Item> T1_NESTS = TagKey.create(Registries.ITEM, ModIdentifier.of("t1_nests"));
    public static final TagKey<Item> T2_NESTS = TagKey.create(Registries.ITEM, ModIdentifier.of("t2_nests"));
    public static final TagKey<Item> T3_NESTS = TagKey.create(Registries.ITEM, ModIdentifier.of("t3_nests"));
    public static final TagKey<Item> T4_NESTS = TagKey.create(Registries.ITEM, ModIdentifier.of("t4_nests"));
    public static final TagKey<Item> HONEYCOMBS = commonTag("honeycombs");
    public static final TagKey<Item> HONEYCOMB_STORAGE_BLOCKS = commonTag("storage_blocks/honeycombs");
    public static final TagKey<Item> HONEY_BUCKETS = commonTag("buckets/honey");
    public static final TagKey<Item> HONEY_BLOCKS = commonTag("honey_blocks");
    public static final TagKey<Item> HONEY_BOTTLES = commonTag("honey_bottles");
    public static final TagKey<Item> WAX = commonTag("wax");
    public static final TagKey<Item> WAX_STORAGE_BLOCKS = commonTag("storage_blocks/wax");
    public static final TagKey<Item> HEAT_SOURCES = commonTag("heat_sources");


    private ModItemTags() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static TagKey<Item> commonTag(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
}
