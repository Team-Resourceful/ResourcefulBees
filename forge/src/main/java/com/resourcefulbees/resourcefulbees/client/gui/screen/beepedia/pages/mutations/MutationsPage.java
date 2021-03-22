package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;

public abstract class MutationsPage {
    protected final BeepediaScreen beepedia;
    MutationTypes type;
    CustomBeeData beeData;
    protected int inputCounter;
    protected int outputCounter;
    protected final ResourceLocation infoIcon = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    protected MutationsPage(MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        this.type = type;
        this.beeData = beeData;
        this.beepedia = beepedia;
    }

    public abstract void tick(int ticksActive);

    public abstract void draw(PoseStack matrix, int xPos, int yPos);

    public abstract boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY);

    public abstract void drawTooltips(PoseStack matrix, int xPos, int yPos, int mouseX, int mouseY);

    protected void drawWeight(PoseStack matrix, Double right, int xPos, int yPos) {
        Font font = Minecraft.getInstance().font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        TextComponent text = new TextComponent(decimalFormat.format(right));
        int padding = font.width(text) / 2;
        font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos - padding, yPos, -1);
    }
    protected void drawChance(PoseStack matrix, Double right, int xPos, int yPos) {
        if (right >= 1) return;
        drawWeight(matrix, right, xPos, yPos);
    }


    public abstract String getSearch();
}
