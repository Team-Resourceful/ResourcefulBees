package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats;

import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
