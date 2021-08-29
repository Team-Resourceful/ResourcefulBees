package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats;

import com.teamresourceful.resourcefulbees.utils.CombBlockPair;

public class CombBeepediaStats extends BeepediaStats {

    public final CombBlockPair combData;

    public CombBeepediaStats(CombBlockPair combData) {
        this.combData = combData;
    }

    @Override
    public void initSearchTerms() {

    }

    @Override
    boolean isValidSearch(String search) {
        return false;
    }
}
