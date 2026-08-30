package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    public static final TagKey<Item> HONEY_BOTTLES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "honey_bottles"));

    private ModItemTags() throws UtilityClassException {
        throw new UtilityClassException();
    }
}
