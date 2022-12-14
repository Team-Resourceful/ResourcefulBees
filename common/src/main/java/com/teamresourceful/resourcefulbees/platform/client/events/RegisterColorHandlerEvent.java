package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * The block map should only be used as a reference, not for registering blocks when phase is ITEMS.
 */
public record RegisterColorHandlerEvent(ItemColors items, BlockColors blocks, Phase phase) {

    public static final EventHelper<RegisterColorHandlerEvent> EVENT = new EventHelper<>();

    public void register(ItemColor itemColor, ItemLike... item) {
        if (phase == Phase.BLOCKS) throw new IllegalStateException("Cannot register item colors during block color registration");
        items.register(itemColor, item);
    }

    public void register(BlockColor blockColor, Block... block) {
        if (phase == Phase.ITEMS) throw new IllegalStateException("Cannot register block colors during item color registration");
        blocks.register(blockColor, block);
    }

    public enum Phase {
        ITEMS,
        BLOCKS,
    }
}
