package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeeDataPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;

public abstract class MutationPage extends BeeDataPage {

    protected MutationPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, yPos, xPos, parent);
    }
}
