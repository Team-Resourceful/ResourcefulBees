package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * The block map should only be used as a reference, not for registering blocks when phase is ITEMS.
 */
public record RegisterColorHandlerEvent(ItemRegister items, BlockRegister blocks) {

    public static final EventHelper<RegisterColorHandlerEvent> EVENT = new EventHelper<>();

    public void register(ItemColor itemColor, ItemLike... item) {
        if (items == null) throw new IllegalStateException("Cannot register item colors when registration is disabled");
        items.register(itemColor, item);
    }

    public void register(BlockColor blockColor, Block... block) {
        if (blocks == null) throw new IllegalStateException("Cannot register block colors when registration is disabled");
        blocks.register(blockColor, block);
    }

    public interface ItemRegister {
        void register(ItemColor itemColor, ItemLike... item);
    }

    public interface BlockRegister {
        void register(BlockColor blockColor, Block... block);
    }
}
