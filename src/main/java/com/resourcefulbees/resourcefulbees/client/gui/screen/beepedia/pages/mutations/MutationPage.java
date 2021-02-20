package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeeDataPage;

public abstract class MutationPage extends BeeDataPage {

    private CustomBeeData beeData;

    public MutationPage(BeepediaScreen beepedia, CustomBeeData beeData) {
        super(beepedia, BeepediaScreen.Page.MUTATION, beeData);
    }
}
