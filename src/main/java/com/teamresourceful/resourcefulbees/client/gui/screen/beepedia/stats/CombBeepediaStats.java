package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats;

import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;

public class CombBeepediaStats extends BeepediaStats {

    public final OutputVariation variation;

    public CombBeepediaStats(OutputVariation variation) {
        this.variation = variation;
    }

    @Override
    public void initSearchTerms() {

    }

    @Override
    boolean isValidSearch(String search) {
        return false;
    }
}
