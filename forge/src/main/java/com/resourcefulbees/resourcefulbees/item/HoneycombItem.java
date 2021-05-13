package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneycombData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class HoneycombItem extends Item {

    private final HoneycombData honeycombData;
    private final String beeType;

    public HoneycombItem(String beeType, HoneycombData honeycombData, Item.Properties properties) {
        super(properties);
        this.honeycombData = honeycombData;
        this.beeType = beeType;
    }

    @SuppressWarnings("unusedParameter")
    public static int getColor(ItemStack stack, int tintIndex) {
        HoneycombItem honeycombItem = (HoneycombItem) stack.getItem();
        return honeycombItem.honeycombData.isRainbow() ? RainbowColor.getRGB() : honeycombItem.getHoneycombColor();
    }

    public int getHoneycombColor() { return honeycombData.getColor().getC(); }

    public String getBeeType() { return beeType; }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties() {
        return new FoodProperties.Builder()
                .nutrition(Config.HONEYCOMB_HUNGER.get())
                .saturationMod(Config.HONEYCOMB_SATURATION.get().floatValue())
                .fast()
                .build();
    }
}
