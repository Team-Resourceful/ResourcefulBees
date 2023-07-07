package com.teamresourceful.resourcefulbees.common.items.base;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;

public interface Tradeable {

    boolean isTradable();

    BeekeeperTradeData getTradeData();
}
