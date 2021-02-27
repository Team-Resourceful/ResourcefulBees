package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.MutationPage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MutationListPage extends BeeDataPage {

    Map<MutationTypes, List<MutationPage>> mutations = new EnumMap<>(MutationTypes.class);

    public MutationListPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        this.beeData = beeData;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations");
        font.draw(matrix, title, xPos, (float)yPos + 8f, TextFormatting.WHITE.getColor());
    }

}
