package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.MutationPage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutationListPage extends BeepediaPage {

    private CustomBeeData beeData;
    Map<MutationTypes, List<MutationPage>> mutations = new HashMap<>();

    public MutationListPage(BeepediaScreen beepedia, CustomBeeData beeData) {
        super(beepedia, BeepediaScreen.Page.MUTATION_LIST);
        this.beeData = beeData;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public String getTranslation() {
        return beeData.getTranslation().getString();
    }
}
