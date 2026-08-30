package com.teamresourceful.resourcefulbees.api.data;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public interface BeekeeperTradeData extends BeeData<BeekeeperTradeData> {

    boolean isTradable();

    UniformGenerator amount();

    Item secondaryItem();

    UniformGenerator secondaryItemCost();

    float priceMultiplier();

    int maxTrades();

    int xp();

    //MerchantOffer getMerchantOffer(RandomSource random, ItemStack product, int flowerMin, int flowerMax);
}
