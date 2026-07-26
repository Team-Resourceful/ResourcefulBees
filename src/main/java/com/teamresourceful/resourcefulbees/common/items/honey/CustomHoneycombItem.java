package com.teamresourceful.resourcefulbees.common.items.honey;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.common.config.HoneycombConfig;
import com.teamresourceful.resourcefulbees.common.items.base.Tradeable;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CustomHoneycombItem extends HoneycombItem implements Tradeable, ColoredObject {

    private final Supplier<Item> storageBlock;
    private final Color color;
    private final BeekeeperTradeData tradeData;

    public CustomHoneycombItem(Color color, Supplier<Item> storageBlock, BeekeeperTradeData tradeData, Properties properties) {
        super(properties);
        this.color = color;
        this.storageBlock = storageBlock;
        this.tradeData = tradeData;
    }

    public int color() {
        return color.getOpaqueValue();
    }

    public Item getStorageBlockItem() {
        return storageBlock.get();
    }

    public boolean hasStorageBlockItem() {
        return storageBlock != null;
    }

    @Override
    public boolean isTradable() {
        return tradeData.isTradable();
    }

    @Override
    public BeekeeperTradeData getTradeData() {
        return tradeData;
    }
}
