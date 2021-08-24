package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class BeeDataPage extends BeepediaPage {


    public CustomBeeData beeData;

    public BeeDataPage(BeepediaScreenArea screenArea) {
        super(screenArea);
    }

    @OverridingMethodsMustInvokeSuper
    protected void preInit(BeepediaScreen beepedia, BeepediaScreenArea screenArea, CustomBeeData beeData) {
        super.preInit(beepedia);
        this.beeData = beeData;
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        // override to implement
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        // override to implement
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }
}
