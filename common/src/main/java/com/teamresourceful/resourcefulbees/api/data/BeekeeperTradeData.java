package com.teamresourceful.resourcefulbees.api.data;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public interface BeekeeperTradeData extends BeeData<BeekeeperTradeData> {

    boolean isTradable();

    UniformInt amount();

    ItemStack secondaryItem();

    UniformInt secondaryItemCost();

    float priceMultiplier();

    int maxTrades();

    int xp();

    MerchantOffer getMerchantOffer(RandomSource random, ItemStack product, int flowerMin, int flowerMax);
}
