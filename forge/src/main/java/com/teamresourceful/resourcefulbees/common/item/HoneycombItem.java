package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.common.config.HoneycombConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class HoneycombItem extends net.minecraft.world.item.HoneycombItem {

    private final Supplier<Item> storageBlock;
    private final Color color;
    private final boolean isEdible;
    private final boolean enchanted;
    private final BeekeeperTradeData tradeData;

    public HoneycombItem(Color color, boolean isEdible, Supplier<Item> storageBlock, boolean enchanted, BeekeeperTradeData tradeData) {
        super(new Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_COMBS));
        this.color = color;
        this.isEdible = isEdible;
        this.storageBlock = storageBlock;
        this.enchanted = enchanted;
        this.tradeData = tradeData;
    }

    @SuppressWarnings("unused")
    public static int getColor(ItemStack stack, int tintIndex) {
        return ((HoneycombItem) stack.getItem()).getHoneycombColor();
    }

    public int getHoneycombColor() { return color.getValue(); }

    public Item getStorageBlockItem() {
        return storageBlock.get();
    }

    public boolean hasStorageBlockItem() {
        return storageBlock != null;
    }

    public boolean isTradable() {
        return tradeData.isTradable();
    }

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
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        if (HoneycombConfig.honeycombsEdible && !isEdible) {
            return super.getFoodProperties(stack, entity);
        }
        return new FoodProperties.Builder()
                .nutrition(HoneycombConfig.honeycombHunger)
                .saturationMod(HoneycombConfig.honeycombSaturation)
                .fast()
                .build();
    }
}
