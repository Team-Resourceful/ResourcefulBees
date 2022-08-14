package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.*;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HomePage extends BeepediaPage {

    int counter = 0;
    List<Entity> bees;
    private ModImageButton discord;
    private ModImageButton patreon;
    private ModImageButton issues;
    private ModImageButton wiki;

    public HomePage(ScreenArea screenArea) {
        super(screenArea);
    }

    @Override
    public void registerScreen(BeepediaScreen beepedia) {
        super.registerScreen(beepedia);
        counter = 0;
        int xPos = x;
        int yPos = y;
        discord = new ModImageButton(xPos + 169, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.DISCORD_BUTTON,
                onPress -> Util.getPlatform().openUri("https://discord.resourcefulbees.com"), BeepediaLang.TOOLTIP_DISCORD);
        patreon = new ModImageButton(xPos + 144, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.PATREON_BUTTON,
                onPress -> Util.getPlatform().openUri("https://patreon.resourcefulbees.com"), BeepediaLang.TOOLTIP_PATREON);
        issues = new ModImageButton(xPos + 119, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.ISSUES_BUTTON,
                onPress -> Util.getPlatform().openUri("https://issues.resourcefulbees.com"), BeepediaLang.TOOLTIP_ISSUES);
        wiki = new ModImageButton(xPos + 94, yPos + 149, 25, 25, 0, 0, 25, BeepediaImages.WIKI_BUTTON,
                onPress -> Util.getPlatform().openUri("https://wiki.resourcefulbees.com"), BeepediaLang.TOOLTIP_WIKI);
        children.add(discord);
        children.add(patreon);
        children.add(issues);
        children.add(wiki);
        bees = new LinkedList<>();
        BeepediaHandler.beeStats.forEach((s, b) -> bees.add(b.getBee()));
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScale();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (y + 80) * scale);
        GL11.glScissor((int) (x * scale), scissorY, (int) (width * scale), (int) ((73) * scale));
        ClientUtils.renderEntity(matrix, bees.get(counter), x + (width / 2F) - 12F, y + 10f, -45, 3);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        Font font = Minecraft.getInstance().font;
        Component completeStatus = getProgress();
        int padding = font.width(completeStatus) / 2;
        font.draw(matrix, completeStatus, x + (width / 2F) - padding, y + 115F, -1);
        font.draw(matrix, BeepediaLang.ITEM_GROUP.withStyle(ChatFormatting.GRAY), x + 52F, y + 81F, -1);
        ClientUtils.bindTexture(BeepediaImages.LOGO);
        Gui.blit(matrix, x + (width / 2) - 52, y + 90, 0, 0, 104, 16, 104, 16);
    }

    private Component getProgress() {
        MutableComponent prefix = BeepediaLang.COLLECTION_PROGRESS.plainCopy();
        prefix.append(String.format("%d / %d", beepedia.isCreative ? bees.size() : beepedia.data.getBeeList().size(), bees.size()));
        prefix.withStyle(ChatFormatting.GRAY);
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
    public void addSearch(BeepediaPage parent) {

    }
}
