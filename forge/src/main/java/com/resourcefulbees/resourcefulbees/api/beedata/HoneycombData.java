package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutputs;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;


public class HoneycombData {

    public static final List<Integer> DEFAULT_APIARY_AMOUNTS = Arrays.asList(Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get());
    public static final List<ApiaryOutputs> DEFAULT_APIARY_OUTPUTS = Arrays.asList(Config.T1_APIARY_OUTPUT.get(), Config.T2_APIARY_OUTPUT.get(), Config.T3_APIARY_OUTPUT.get(), Config.T4_APIARY_OUTPUT.get());


    public static final Codec<HoneycombData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HoneycombTypes.CODEC.fieldOf("honeycombType").orElse(HoneycombTypes.DEFAULT).forGetter(HoneycombData::getHoneycombType),
            Registry.ITEM.fieldOf("honeycomb").orElse(Items.BARRIER).forGetter(HoneycombData::getHoneycomb),
            Registry.ITEM.fieldOf("honeycombBlock").orElse(Items.BARRIER).forGetter(HoneycombData::getHoneycombBlock),
            Color.CODEC.fieldOf("color").orElse(Color.WHITE).forGetter(HoneycombData::getColor),
            Codec.BOOL.fieldOf("isColored").orElse(true).forGetter(HoneycombData::isColored),
            Codec.BOOL.fieldOf("isRainbow").orElse(false).forGetter(HoneycombData::isColored),
            Codec.INT.listOf().fieldOf("apiaryOutputAmounts").orElse(DEFAULT_APIARY_AMOUNTS).forGetter(HoneycombData::getApiaryOutputAmounts),
            ApiaryOutputs.CODEC.listOf().fieldOf("apiaryOutputTypes").orElse(DEFAULT_APIARY_OUTPUTS).forGetter(HoneycombData::getApiaryOutputTypes)
    ).apply(instance, HoneycombData::new));

    public static final HoneycombData DEFAULT = new HoneycombData(HoneycombTypes.NONE, Items.AIR, Items.AIR, Color.WHITE, false, false, DEFAULT_APIARY_AMOUNTS, DEFAULT_APIARY_OUTPUTS);

    private final HoneycombTypes honeycombType;
    private final Item honeycomb;
    private final Item honeycombBlock;
    private final Color color;
    private final boolean isColored;
    private final boolean isRainbow;
    private final List<Integer> apiaryOutputAmounts;
    private final List<ApiaryOutputs> apiaryOutputsTypes;


    public HoneycombData(HoneycombTypes honeycombType, Item honeycomb, Item honeycombBlock, Color color, boolean isColored, boolean isRainbow, List<Integer> apiaryOutputAmounts, List<ApiaryOutputs> apiaryOutputsTypes) {
        this.honeycombType = honeycombType;
        this.honeycomb = honeycomb;
        this.honeycombBlock = honeycombBlock;
        this.color = color;
        this.isColored = isColored;
        this.isRainbow = isRainbow;
        this.apiaryOutputAmounts = apiaryOutputAmounts;
        this.apiaryOutputsTypes = apiaryOutputsTypes;
    }

    public HoneycombTypes getHoneycombType() {
        return honeycombType;
    }

    public Item getHoneycomb() {
        return honeycomb;
    }

    public Item getHoneycombBlock() {
        return honeycombBlock;
    }

    public Color getColor() {
        return color;
    }

    public boolean isColored() {
        return isColored;
    }

    public boolean isRainbow() {
        return isRainbow;
    }

    public List<Integer> getApiaryOutputAmounts() {
        return apiaryOutputAmounts;
    }

    public List<ApiaryOutputs> getApiaryOutputTypes() {
        return apiaryOutputsTypes;
    }
}
