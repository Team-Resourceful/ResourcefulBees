package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    public static final TagKey<Item> HONEYCOMB_BLOCK = createItemTag("rbees_common", "storage_blocks/honeycombs");
    public static final TagKey<Item> HONEYCOMB = createItemTag("rbees_common", "honeycombs");
    public static final TagKey<Item> WAX = createItemTag("rbees_common", "wax");
    public static final TagKey<Item> WAX_BLOCK = createItemTag("rbees_common", "storage_blocks/wax");
    public static final TagKey<Item> SHEARS = createItemTag("rbees_common", "shears");
    public static final TagKey<Item> BEEHIVES = createItemTag("minecraft", "beehives");

    public static final TagKey<Item> T0_NESTS = createItemTag(ModConstants.MOD_ID, "t0_nests");
    public static final TagKey<Item> T1_NESTS = createItemTag(ModConstants.MOD_ID, "t1_nests");
    public static final TagKey<Item> T2_NESTS = createItemTag(ModConstants.MOD_ID, "t2_nests");
    public static final TagKey<Item> T3_NESTS = createItemTag(ModConstants.MOD_ID, "t3_nests");

    public static final TagKey<Item> MUSHROOM = createItemTag("rbees_common", "mushrooms");
    public static final TagKey<Item> HONEY_BOTTLES = createItemTag("rbees_common", "honey_bottles");
    public static final TagKey<Item> HONEY_BLOCKS = createItemTag("rbees_common", "honey_blocks");
    public static final TagKey<Item> OBSIDIAN = createItemTag("rbees_common", "obsidian");
    public static final TagKey<Item> NETHER_STARS = createItemTag("rbees_common", "nether_stars");
    public static final TagKey<Item> GLASS = createItemTag("rbees_common", "glass");
    public static final TagKey<Item> COLORLESS_GLASS = createItemTag("rbees_common", "glass_colorless");
    public static final TagKey<Item> GLASS_PANES = createItemTag("rbees_common", "glass_panes");
    public static final TagKey<Item> COLORLESS_GLASS_PANES = createItemTag("rbees_common", "glass_colorless_panes");
    public static final TagKey<Item> CHESTS = createItemTag("rbees_common", "chests");

    private ModItemTags() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static TagKey<Item> createItemTag(String mod, String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(mod, path));
    }
}
