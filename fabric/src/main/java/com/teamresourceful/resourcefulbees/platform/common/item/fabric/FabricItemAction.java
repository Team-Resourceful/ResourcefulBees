package com.teamresourceful.resourcefulbees.platform.common.item.fabric;

import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FabricItemAction implements ItemAction {

    private final String id;
    private final TagKey<Item> tag;

    public FabricItemAction(String id) {
        this.id = id;
        this.tag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", "actions/" + id));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TagKey<Item> getTag() {
        return tag;
    }
}
