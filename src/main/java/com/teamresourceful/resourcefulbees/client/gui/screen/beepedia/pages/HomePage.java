package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaImages;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.search.HoneyBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class HomePage extends BeepediaPage {

    int counter = 0;
    List<BeePage> bees;
    private ModImageButton discord;
    private ModImageButton patreon;
    private ModImageButton issues;
    private ModImageButton wiki;

    final ResourceLocation logo = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/logo.png");

    public HomePage(BeepediaScreenArea screenArea) {
        super(screenArea);
    }

    @Override
    public void registerButtons(BeepediaScreen beepedia) {
        int xPos = screenArea.getX(beepedia);
        int yPos = screenArea.getY(beepedia);
        discord = new ModImageButton(xPos + 139, yPos + 129, 25, 25, 0, 0, 25, BeepediaImages.DISCORD_BUTTON,
                onPress -> Util.getPlatform().openUri("https://discord.resourcefulbees.com"));
        patreon = new ModImageButton(xPos + 114, yPos + 129, 25, 25, 0, 0, 25, BeepediaImages.PATREON_BUTTON,
                onPress -> Util.getPlatform().openUri("https://patreon.resourcefulbees.com"));
        issues = new ModImageButton(xPos + 89, yPos + 129, 25, 25, 0, 0, 25, BeepediaImages.ISSUES_BUTTON,
                onPress -> Util.getPlatform().openUri("https://issues.resourcefulbees.com"));
        wiki = new ModImageButton(xPos + 64, yPos + 129, 25, 25, 0, 0, 25, BeepediaImages.WIKI_BUTTON,
                onPress -> Util.getPlatform().openUri("https://wiki.resourcefulbees.com"));
        pageButtons.add(discord);
        pageButtons.add(patreon);
        pageButtons.add(issues);
        pageButtons.add(wiki);
        beepedia.addButtons(pageButtons);
    }

    @Override
    public void preInit(BeepediaScreen beepedia, HoneyBeepediaStats traitBeepediaStats) {
        super.preInit(beepedia, traitBeepediaStats);
        bees = new LinkedList<>();
        beepedia.bees.forEach((s, b) -> bees.add(b));
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScale();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (this.yPos + 80) * scale);
        GL11.glScissor((int) (this.xPos * scale), scissorY, (int) (SUB_PAGE_WIDTH * scale), (int) ((73) * scale));
        RenderUtils.renderEntity(matrix, bees.get(counter).getBee(), beepedia.getMinecraft().level, xPos + (SUB_PAGE_WIDTH / 2F) - 12F, yPos + 10f, -45, 3);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        FontRenderer font = Minecraft.getInstance().font;
        ITextComponent completeStatus = getProgress();
        int padding = font.width(completeStatus) / 2;
        font.draw(matrix, completeStatus, xPos + (SUB_PAGE_WIDTH / 2F) - padding, yPos + 115F, -1);
        font.draw(matrix, new TranslationTextComponent("itemGroup.resourcefulbees").withStyle(TextFormatting.GRAY), xPos + 32F, yPos + 81F, -1);
        Minecraft.getInstance().getTextureManager().bind(logo);
        AbstractGui.blit(matrix, xPos + (SUB_PAGE_WIDTH / 2) - 52, yPos + 90, 0, 0, 104, 16, 104, 16);
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {

    }

    private ITextComponent getProgress() {
        TranslationTextComponent prefix = new TranslationTextComponent("gui.resourcefulbees.beepedia.home.progress");
        prefix.append(String.format("%d / %d", beepedia.complete ? beepedia.bees.size() : beepedia.itemBees.size(), beepedia.bees.size()));
        prefix.withStyle(TextFormatting.GRAY);
        return prefix;
    }

    @Override
    public void addSearch() {
        // does nothing
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
            counter++;
            if (counter >= bees.size()) counter = 0;
            BeepediaScreen.currScreenState.setHomeCounter(counter);
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (BeepediaScreen.mouseHovering(xPos + 139f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.discord"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 114f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.patreon"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 89f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.issues"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 64f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.wiki"), mouseX, mouseY);
        }
    }

    @Override
    public void openPage() {
        super.openPage();
        discord.visible = true;
        patreon.visible = true;
        issues.visible = true;
        wiki.visible = true;
        counter = BeepediaScreen.currScreenState.getHomeCounter();
    }

    @Override
    public void closePage() {
        super.closePage();
        discord.visible = false;
        patreon.visible = false;
        issues.visible = false;
        wiki.visible = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
