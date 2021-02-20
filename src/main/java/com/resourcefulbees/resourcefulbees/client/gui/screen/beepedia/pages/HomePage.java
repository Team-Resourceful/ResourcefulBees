package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.util.text.TranslationTextComponent;

public class HomePage extends BeepediaPage {


    public HomePage(BeepediaScreen beepedia) {
        super(beepedia, BeepediaScreen.Page.HOME);
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public String getTranslation() {
        return new TranslationTextComponent("gui.resourcefulbees.beepedia.home_button").getString();
    }
}
