package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;

public class HoneyBeepediaStats extends BeepediaStats {

    HoneyBottleData honeyData;
    public HoneyBeepediaStats(HoneyBottleData honeyData) {
        this.honeyData = honeyData;
    }

    @Override
    public void initSearchTerms() {

    }

    @Override
    boolean isValidSearch(String search) {
        return false;
    }

}
