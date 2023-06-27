package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;

public interface Tradeable {

    boolean isTradable();

    BeekeeperTradeData getTradeData();
}
