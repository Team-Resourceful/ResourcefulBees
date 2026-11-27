
package com.teamresourceful.resourcefulbees.api.data.honeycomb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.TradeData;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record HoneycombDefinition(
        Identifier name,
        Color color,
        boolean edible,
        boolean block,
        boolean enchanted,
        BeekeeperTradeData tradeData,
        List<Supplier<OutputVariation>> variations
) {

    public static Codec<HoneycombDefinition> codec(Identifier name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(name),
                Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneycombDefinition::color),
                Codec.BOOL.fieldOf("edible").orElse(true).forGetter(HoneycombDefinition::edible),
                Codec.BOOL.fieldOf("block").orElse(true).forGetter(HoneycombDefinition::block),
                Codec.BOOL.fieldOf("enchanted").orElse(false).forGetter(HoneycombDefinition::enchanted),
                TradeData.CODEC.fieldOf("tradeData").orElse(TradeData.DEFAULT).forGetter(HoneycombDefinition::tradeData)
        ).apply(instance, HoneycombDefinition::new));
    }

    public HoneycombDefinition {
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);
        Objects.requireNonNull(tradeData);
        variations = List.copyOf(variations);
    }

    public HoneycombDefinition(
            Identifier name,
            Color color,
            boolean edible,
            boolean block,
            boolean enchanted,
            BeekeeperTradeData tradeData
    ) {
        this(name, color, edible, block, enchanted, tradeData, List.of());
    }
}
