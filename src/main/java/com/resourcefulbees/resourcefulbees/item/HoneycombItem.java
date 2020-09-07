package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.utils.Color;
import com.resourcefulbees.resourcefulbees.utils.RainbowColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class HoneycombItem extends Item {

    private final ColorData colorData;
    private final String beeType;

    public HoneycombItem(String beeType, ColorData colorData) {
        super(new Properties().group(ItemGroup.MATERIALS));
        this.colorData = colorData;
        this.beeType = beeType;
    }

    public static int getColor(ItemStack stack, int tintIndex){
        HoneycombItem honeycombItem = (HoneycombItem) stack.getItem();
        return honeycombItem.colorData.isRainbowBee() ? RainbowColor.getRGB() : honeycombItem.getHoneycombColor();
    }

    public int getHoneycombColor() { return Color.parseInt(colorData.getHoneycombColor()); }

    public String getBeeType() { return beeType; }

    @Override
    public boolean isFood() {
        return true;
    }

    @Nullable
    @Override
    public Food getFood() {
        return new Food.Builder()
                .hunger(Config.HONEYCOMB_HUNGER.get())
                .saturation(Config.HONEYCOMB_SATURATION.get().floatValue())
                .build();
    }
}
