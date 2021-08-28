package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HoneycombItem extends Item {

    private final Color color;
    private final boolean isEdible;

    public HoneycombItem(Color color, boolean isEdible, Item.Properties properties) {
        super(properties);
        this.color = color;
        this.isEdible = isEdible;
    }

    @SuppressWarnings("unusedParameter")
    public static int getColor(ItemStack stack, int tintIndex) {
        return ((HoneycombItem) stack.getItem()).getHoneycombColor();
    }

    public int getHoneycombColor() { return color.getValue(); }

    @Override
    public boolean isEdible() {
        return isEdible;
    }

    @Nullable
    @Override
    public Food getFoodProperties() {
        if (Boolean.TRUE.equals(Config.EDIBLE_HONEYCOMBS.get()) && !isEdible) {
            return super.getFoodProperties();
        }
        return new Food.Builder()
                .nutrition(Config.HONEYCOMB_HUNGER.get())
                .saturationMod(Config.HONEYCOMB_SATURATION.get().floatValue())
                .fast()
                .build();
    }
}
