package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.bounds.UniformedNumberCodecs;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

public record TradeData(
        UniformInt amount,
        Item secondaryItem,
        UniformInt secondaryItemCost,
        float priceMultiplier,
        int maxTrades,
        int xp
) implements BeekeeperTradeData {

    public MerchantOffer getMerchantOffer(RandomSource random, ItemStack product, int flowerMin, int flowerMax) {
        product.setCount(amount().sample(random));
        return new MerchantOffer(
                new ItemCost(ModItems.GOLD_FLOWER_ITEM.get(), random.nextIntBetweenInclusive(flowerMin, flowerMax)),
                Optional.of(new ItemCost(secondaryItem(), secondaryItemCost().sample(random))),
                product,
                0, maxTrades, xp, priceMultiplier
        );
    }

    @Override
    public boolean isTradable() {
        return this != DEFAULT;
    }

    public static final Codec<BeekeeperTradeData> CODEC = RecordCodecBuilder.create(tradeDataInstance -> tradeDataInstance.group(
            UniformedNumberCodecs.rangedUniformIntCodec(1, 64).optionalFieldOf("amount", UniformInt.of(1,1)).forGetter(BeekeeperTradeData::amount),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("secondaryItem", Items.AIR).forGetter(BeekeeperTradeData::secondaryItem),
            UniformedNumberCodecs.rangedUniformIntCodec(1, 64).optionalFieldOf("secondaryItemCost", UniformInt.of(1, 4)).forGetter(BeekeeperTradeData::secondaryItemCost),
            CodecExtras.NON_NEGATIVE_FLOAT.optionalFieldOf("priceMultiplier", 0.05f).forGetter(BeekeeperTradeData::priceMultiplier),
            Codec.intRange(1, 64).optionalFieldOf("maxTrades", 8).forGetter(BeekeeperTradeData::maxTrades),
            Codec.intRange(1, 64).optionalFieldOf("xp", 3).forGetter(BeekeeperTradeData::xp)
    ).apply(tradeDataInstance, TradeData::new));

    public static final BeekeeperTradeData DEFAULT = new TradeData(UniformInt.of(0,0), Items.AIR, UniformInt.of(0,0), 0, 0, 0);

    public static final BeeDataSerializer<BeekeeperTradeData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("trade"), 1, _ -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeekeeperTradeData> serializer() {
        return SERIALIZER;
    }
}
