package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CentrifugePage extends BeeDataPage {

    public CentrifugePage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
    }


    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge");
        font.draw(matrix, title, xPos, (float)yPos + 8, TextFormatting.WHITE.getColor());
    }
}
