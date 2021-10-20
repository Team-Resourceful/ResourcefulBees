package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.widget.Pane;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class BeepediaPage extends Pane {


    public BeepediaScreen beepedia;

    public BeepediaPage(int xPos, int yPos, int viewWidth, int viewHeight) {
        super(xPos, yPos, viewWidth, viewHeight);
    }

    public BeepediaPage(ScreenArea screenArea) {
        super(screenArea);
    }


    /**
     * register any data or tooltips here.
     */
    public void preInit() {

    }

    /**
     * only register buttons here, this method will only be run when the beepedia screen is opened
     * @param beepedia the main screen class
     */
    @OverridingMethodsMustInvokeSuper
    public void registerScreen(BeepediaScreen beepedia){
        this.beepedia = beepedia;
    }

    public abstract void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY);

    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        // override to implement
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    public abstract void addSearch(BeepediaPage parent);

    public void tick(int ticksActive) {
        // override to implement
    }


}
