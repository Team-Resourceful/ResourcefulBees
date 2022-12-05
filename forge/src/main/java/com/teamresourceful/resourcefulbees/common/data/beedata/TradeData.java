package com.teamresourceful.resourcefulbees.common.data.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefulbees.common.utils.CodecUtils;
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
            CodecUtils.rangedUniformIntCodec(1, 64).fieldOf("amount").orElse(UniformInt.of(1,1)).forGetter(BeekeeperTradeData::amount),
            ItemStackCodec.CODEC.fieldOf("secondaryItem").orElse(ItemStack.EMPTY).forGetter(BeekeeperTradeData::secondaryItem),
            CodecUtils.rangedUniformIntCodec(1, 64).fieldOf("secondaryItemCost").orElse(UniformInt.of(1, 4)).forGetter(BeekeeperTradeData::secondaryItemCost),
            Codec.floatRange(0, 1).fieldOf("priceMultiplier").orElse(0.05f).forGetter(BeekeeperTradeData::priceMultiplier),
            Codec.intRange(1, 64).fieldOf("maxTrades").orElse(4).forGetter(BeekeeperTradeData::maxTrades),
            Codec.intRange(1, 64).fieldOf("xp").orElse(2).forGetter(BeekeeperTradeData::xp)
    ).apply(tradeDataInstance, TradeData::new));

    public static final BeekeeperTradeData DEFAULT = new TradeData(UniformInt.of(0,0), ItemStack.EMPTY, UniformInt.of(0,0), 0, 0, 0);

    public static final BeeDataSerializer<BeekeeperTradeData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("trade"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeekeeperTradeData> serializer() {
        return SERIALIZER;
    }
}
