package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.bounds.UniformedNumberCodecs;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public record TradeData(
        UniformInt amount,
        ItemStack secondaryItem,
        UniformInt secondaryItemCost,
        float priceMultiplier,
        int maxTrades,
        int xp
) implements BeekeeperTradeData {

    public MerchantOffer getMerchantOffer(RandomSource random, ItemStack product, int flowerMin, int flowerMax) {
        product.setCount(amount().sample(random));
        ItemStack secondaryCost = secondaryItem().copy();
        secondaryCost.setCount(secondaryItemCost().sample(random));
        return new MerchantOffer(
                new ItemStack(ModItems.GOLD_FLOWER_ITEM.get(), random.nextIntBetweenInclusive(flowerMin, flowerMax)),
                secondaryCost,
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
            ItemStackCodec.CODEC.optionalFieldOf("secondaryItem", ItemStack.EMPTY).forGetter(BeekeeperTradeData::secondaryItem),
            UniformedNumberCodecs.rangedUniformIntCodec(1, 64).optionalFieldOf("secondaryItemCost", UniformInt.of(1, 4)).forGetter(BeekeeperTradeData::secondaryItemCost),
            CodecExtras.FLOAT_UNIT_INTERVAL.optionalFieldOf("priceMultiplier", 0.05f).forGetter(BeekeeperTradeData::priceMultiplier),
            Codec.intRange(1, 64).optionalFieldOf("maxTrades", 8).forGetter(BeekeeperTradeData::maxTrades),
            Codec.intRange(1, 64).optionalFieldOf("xp", 3).forGetter(BeekeeperTradeData::xp)
    ).apply(tradeDataInstance, TradeData::new));

    public static final BeekeeperTradeData DEFAULT = new TradeData(UniformInt.of(0,0), ItemStack.EMPTY, UniformInt.of(0,0), 0, 0, 0);

    public static final BeeDataSerializer<BeekeeperTradeData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("trade"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeekeeperTradeData> serializer() {
        return SERIALIZER;
    }
}
