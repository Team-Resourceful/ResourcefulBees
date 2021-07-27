package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;

public class HelpPage extends BeepediaPage {

    protected HelpPage(BeepediaScreen beepedia, int xPos, int yPos, String id) {
        super(beepedia, xPos, yPos, id);

    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void addSearch() {
        // do nothing
    }
}
