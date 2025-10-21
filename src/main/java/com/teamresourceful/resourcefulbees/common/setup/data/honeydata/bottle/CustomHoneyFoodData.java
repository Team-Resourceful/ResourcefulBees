package com.teamresourceful.resourcefulbees.common.setup.data.honeydata.bottle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleEffectData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyFoodData;

import java.util.List;

public record CustomHoneyFoodData(int hunger, float saturation, boolean canAlwaysEat, boolean fastFood, List<HoneyBottleEffectData> effects) implements HoneyFoodData {

    public static final CustomHoneyFoodData DEFAULT = new CustomHoneyFoodData(1, 1f, false, false, List.of());
    public static final Codec<HoneyFoodData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("hunger", 1).forGetter(HoneyFoodData::hunger),
            Codec.FLOAT.optionalFieldOf("saturation", 1f).forGetter(HoneyFoodData::saturation),
            Codec.BOOL.optionalFieldOf("canAlwaysEat", false).forGetter(HoneyFoodData::canAlwaysEat),
            Codec.BOOL.optionalFieldOf("fastFood", false).forGetter(HoneyFoodData::fastFood),
            CustomHoneyBottleEffectData.CODEC.listOf().fieldOf("effects").orElse(List.of()).forGetter(HoneyFoodData::effects)
    ).apply(instance, CustomHoneyFoodData::new));

}
