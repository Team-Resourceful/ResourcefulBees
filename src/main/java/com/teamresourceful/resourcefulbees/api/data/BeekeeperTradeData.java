package com.teamresourceful.resourcefulbees.api.data;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

public interface BeekeeperTradeData extends BeeData<BeekeeperTradeData> {

    boolean isTradable();

    Holder<ContextIntProvider> amount();

    Item secondaryItem();

    Holder<ContextIntProvider> secondaryItemCost();

    Holder<ContextFloatProvider> reputationDiscount();

    Holder<ContextIntProvider> maxTrades();

    Holder<ContextIntProvider> xp();

    //MerchantOffer getMerchantOffer(RandomSource random, ItemStack product, int flowerMin, int flowerMax);
}
