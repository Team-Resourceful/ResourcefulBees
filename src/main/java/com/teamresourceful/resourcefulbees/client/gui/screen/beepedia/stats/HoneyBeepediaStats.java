package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoneyBeepediaStats extends BeepediaStats {

    HoneyData honeyData;
    public HoneyBeepediaStats(HoneyData honeyData) {
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
