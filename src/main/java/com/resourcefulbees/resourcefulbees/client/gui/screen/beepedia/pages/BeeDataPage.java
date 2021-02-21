package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;

public abstract class BeeDataPage extends BeepediaPage {

    CustomBeeData beeData;
    BeePage parent;

    public BeeDataPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, xPos, yPos, parent.id);
        this.parent = parent;
        this.beeData = beeData;
    }

    @Override
    public String getSearch() {
        return beeData.getTranslation().getString();
    }
}
