package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.common.config.HoneycombConfig;
import com.teamresourceful.resourcefulbees.common.items.honey.ColoredObject;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CustomHoneycombItem extends HoneycombItem implements Tradeable, ColoredObject {

    private final Supplier<Item> storageBlock;
    private final Color color;
    private final boolean isEdible;
    private final boolean enchanted;
    private final BeekeeperTradeData tradeData;

    public CustomHoneycombItem(Color color, boolean isEdible, Supplier<Item> storageBlock, boolean enchanted, BeekeeperTradeData tradeData) {
        super(new Properties());
        this.color = color;
        this.isEdible = isEdible;
        this.storageBlock = storageBlock;
        this.enchanted = enchanted;
        this.tradeData = tradeData;
    }

    @Override
    public int getObjectColor(int index) {
        return color.getValue();
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

    @Override
    public boolean isEdible() {
        return isEdible;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return this.enchanted || stack.isEnchanted();
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties() {
        if (HoneycombConfig.honeycombsEdible && !isEdible) {
            return super.getFoodProperties();
        }
        return new FoodProperties.Builder()
                .nutrition(HoneycombConfig.honeycombHunger)
                .saturationMod(HoneycombConfig.honeycombSaturation)
                .fast()
                .build();
    }
}
