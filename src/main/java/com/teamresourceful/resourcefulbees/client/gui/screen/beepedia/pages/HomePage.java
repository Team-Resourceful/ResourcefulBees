package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.*;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class HomePage extends BeepediaPage {

    int counter = 0;
    List<Entity> bees;
    private ModImageButton discord;
    private ModImageButton patreon;
    private ModImageButton issues;
    private ModImageButton wiki;

    public HomePage(BeepediaScreenArea screenArea) {
        super(screenArea);
    }

    @Override
    public void registerScreen(BeepediaScreen beepedia) {
        super.registerScreen(beepedia);
        counter = 0;
        int xPos = screenArea.getX(beepedia);
        int yPos = screenArea.getY(beepedia);
        discord = new ModImageButton(xPos + 169, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.DISCORD_BUTTON,
                onPress -> Util.getPlatform().openUri("https://discord.resourcefulbees.com"), BeepediaLang.TOOLTIP_DISCORD);
        patreon = new ModImageButton(xPos + 144, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.PATREON_BUTTON,
                onPress -> Util.getPlatform().openUri("https://patreon.resourcefulbees.com"), BeepediaLang.TOOLTIP_PATREON);
        issues = new ModImageButton(xPos + 119, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.ISSUES_BUTTON,
                onPress -> Util.getPlatform().openUri("https://issues.resourcefulbees.com"), BeepediaLang.TOOLTIP_ISSUES);
        wiki = new ModImageButton(xPos + 94, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.WIKI_BUTTON,
                onPress -> Util.getPlatform().openUri("https://wiki.resourcefulbees.com"), BeepediaLang.TOOLTIP_WIKI);
        pageButtons.add(discord);
        pageButtons.add(patreon);
        pageButtons.add(issues);
        pageButtons.add(wiki);
        beepedia.addButtons(pageButtons);
        bees = new LinkedList<>();
        BeepediaHandler.beeStats.forEach((s, b) -> bees.add(b.getBee()));
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScale();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (getYPos() + 80) * scale);
        GL11.glScissor((int) (getXPos() * scale), scissorY, (int) (screenArea.width * scale), (int) ((73) * scale));
        RenderUtils.renderEntity(matrix, bees.get(counter), beepedia.getMinecraft().level, getXPos() + (screenArea.width / 2F) - 12F, getYPos() + 10f, -45, 3);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        FontRenderer font = Minecraft.getInstance().font;
        ITextComponent completeStatus = getProgress();
        int padding = font.width(completeStatus) / 2;
        font.draw(matrix, completeStatus, getXPos() + (screenArea.width / 2F) - padding, getYPos() + 115F, -1);
        font.draw(matrix, BeepediaLang.ITEM_GROUP.withStyle(TextFormatting.GRAY), getXPos() + 52F, getYPos() + 81F, -1);
        Minecraft.getInstance().getTextureManager().bind(BeepediaImages.LOGO);
        AbstractGui.blit(matrix, getXPos() + (screenArea.width / 2) - 52, getYPos() + 90, 0, 0, 104, 16, 104, 16);
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        // todo remove drawTooltips and replace with registering tooltips
    }

    private ITextComponent getProgress() {
        TranslationTextComponent prefix = BeepediaLang.COLLECTION_PROGRESS.plainCopy();
        prefix.append(String.format("%d / %d", beepedia.isCreative ? bees.size() : beepedia.data.getBeeList().size(), bees.size()));
        prefix.withStyle(TextFormatting.GRAY);
        return prefix;
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
            counter++;
            if (counter >= bees.size()) counter = 0;
        }
    }

    @Override
    public void openPage() {
        super.openPage();
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
