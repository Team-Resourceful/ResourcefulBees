package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.search;

import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;

public class TraitBeepediaStats extends BeepediaStats {

    BeeTrait traitData;

    public TraitBeepediaStats(BeeTrait traitData) {
        this.traitData = traitData;
    }

    @Override
    public void initSearchTerms() {

    }

    @Override
    boolean isValidSearch(String search) {
        return false;
    }
}
