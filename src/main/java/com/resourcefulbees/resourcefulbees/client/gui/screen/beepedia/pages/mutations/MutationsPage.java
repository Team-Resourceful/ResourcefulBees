package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

public abstract class MutationsPage {
    protected final BeepediaScreen beepedia;
    MutationTypes type;
    CustomBeeData beeData;
    protected int inputCounter;
    protected int outputCounter;

    public MutationsPage(MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        this.type = type;
        this.beeData = beeData;
        this.beepedia = beepedia;
    }

    public abstract void tick(int ticksActive);

    public abstract void draw(MatrixStack matrix, int xPos, int yPos);

    public abstract boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY);

    public abstract void drawTooltips(MatrixStack matrix, int mouseX, int mouseY);
}
