package com.teamresourceful.resourcefulbees.item;

import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.config.Config;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class HoneycombItem extends Item {

    private final HoneycombData honeycombData;
    private final String beeType;

    public HoneycombItem(String beeType, HoneycombData honeycombData, Item.Properties properties) {
        super(properties);
        this.honeycombData = honeycombData;
        this.beeType = beeType;
    }

    public HoneycombData getHoneycombData() {
        return honeycombData;
    }

    @SuppressWarnings("unusedParameter")
    public static int getColor(ItemStack stack, int tintIndex) {
        HoneycombItem honeycombItem = (HoneycombItem) stack.getItem();
        return honeycombItem.getHoneycombColor();
    }

    public int getHoneycombColor() { return honeycombData.getColor().getValue(); }

    public String getBeeType() { return beeType; }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Nullable
    @Override
    public Food getFoodProperties() {
        if (Config.EDIBLE_HONEYCOMBS.get() && !honeycombData.isEdible()) {
            return super.getFoodProperties();
        }
        return new Food.Builder()
                .nutrition(Config.HONEYCOMB_HUNGER.get())
                .saturationMod(Config.HONEYCOMB_SATURATION.get().floatValue())
                .fast()
                .build();
    }
}
