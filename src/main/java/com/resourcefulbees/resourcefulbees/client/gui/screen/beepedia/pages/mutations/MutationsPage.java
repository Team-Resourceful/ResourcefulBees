package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;

public abstract class MutationsPage {
    protected final BeepediaScreen beepedia;
    MutationTypes type;
    CustomBeeData beeData;
    protected int inputCounter;
    protected int outputCounter;
    protected final ResourceLocation infoIcon = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    public MutationsPage(MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        this.type = type;
        this.beeData = beeData;
        this.beepedia = beepedia;
    }

    public abstract void tick(int ticksActive);

    public abstract void draw(MatrixStack matrix, int xPos, int yPos);

    public abstract boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY);

    public abstract void drawTooltips(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY);

    protected void drawWeight(MatrixStack matrix, Double right, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        StringTextComponent text = new StringTextComponent(decimalFormat.format(right));
        int padding = font.getWidth(text) / 2;
        font.draw(matrix, text, (float) xPos - padding, yPos, TextFormatting.GRAY.getColor());
    }
    protected void drawChance(MatrixStack matrix, Double right, int xPos, int yPos) {
        if (right >= 1) return;
        drawWeight(matrix, right, xPos, yPos);
    }


}
