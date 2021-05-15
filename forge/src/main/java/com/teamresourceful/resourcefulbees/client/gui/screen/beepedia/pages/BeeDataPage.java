package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;

public abstract class BeeDataPage extends BeepediaPage {

    CustomBeeData beeData;
    final BeePage parent;


    protected BeeDataPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, xPos, yPos, parent.id);
        this.parent = parent;
        this.beeData = beeData;
    }
}
