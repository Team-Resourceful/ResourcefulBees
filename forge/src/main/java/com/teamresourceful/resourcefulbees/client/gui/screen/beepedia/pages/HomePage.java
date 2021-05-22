package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class HomePage extends BeepediaPage {

    private static final ResourceLocation discordButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/discord.png");
    private static final ResourceLocation patreonButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/patreon.png");
    private static final ResourceLocation issuesButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/issues.png");
    private static final ResourceLocation wikiButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/wiki.png");


    int counter = 0;
    final List<BeePage> bees;
    final ModImageButton discord;
    final ModImageButton patreon;
    final ModImageButton issues;
    final ModImageButton wiki;

    final ResourceLocation logo = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/logo.png");

    public HomePage(BeepediaScreen beepedia, int left, int top) {
        super(beepedia, left, top, "home");
        bees = new LinkedList<>();
        beepedia.bees.forEach((s, b) -> bees.add(b));
        discord = new ModImageButton(xPos + 139, yPos + 129, 25, 25, 0, 0, 25, discordButton,
                onPress -> Util.getPlatform().openUri("https://discord.resourcefulbees.com"));
        patreon = new ModImageButton(xPos + 114, yPos + 129, 25, 25, 0, 0, 25, patreonButton,
                onPress -> Util.getPlatform().openUri("https://patreon.resourcefulbees.com"));
        issues = new ModImageButton(xPos + 89, yPos + 129, 25, 25, 0, 0, 25, issuesButton,
                onPress -> Util.getPlatform().openUri("https://issues.resourcefulbees.com"));
        wiki = new ModImageButton(xPos + 64, yPos + 129, 25, 25, 0, 0, 25, wikiButton,
                onPress -> Util.getPlatform().openUri("https://wiki.resourcefulbees.com"));
        beepedia.addButton(discord);
        discord.visible = false;
        beepedia.addButton(patreon);
        patreon.visible = false;
        beepedia.addButton(issues);
        issues.visible = false;
        beepedia.addButton(wiki);
        wiki.visible = false;
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScale();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (this.yPos + 80) * scale);
        GL11.glScissor((int) (this.xPos * scale), scissorY, (int) (SUB_PAGE_WIDTH * scale), (int) ((73) * scale));
        RenderUtils.renderEntity(matrix, bees.get(counter).getBee(), beepedia.getMinecraft().level, xPos + (SUB_PAGE_WIDTH / 2F) - 12F, yPos + 10f, -45, 3);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        Font font = Minecraft.getInstance().font;
        MutableComponent completeStatus = getProgress();
        int padding = font.width(completeStatus) / 2;
        font.draw(matrix, completeStatus, xPos + (SUB_PAGE_WIDTH / 2F) - padding, yPos + 115F, -1);
        font.draw(matrix, new TranslatableComponent("itemGroup.resourcefulbees").withStyle(ChatFormatting.GRAY), xPos + 32F, yPos + 81F, -1);
        Minecraft.getInstance().getTextureManager().bind(logo);
        GuiComponent.blit(matrix, xPos + (SUB_PAGE_WIDTH / 2) - 52, yPos + 90, 0, 0, 104, 16, 104, 16);
    }

    private MutableComponent getProgress() {
        TranslatableComponent prefix = new TranslatableComponent("gui.resourcefulbees.beepedia.home.progress");
        prefix.append(String.format("%d / %d", beepedia.complete ? beepedia.bees.size() : beepedia.itemBees.size(), beepedia.bees.size()));
        prefix.withStyle(ChatFormatting.GRAY);
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
    public void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
        if (BeepediaScreen.mouseHovering(xPos + 139f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslatableComponent("gui.resourcefulbees.beepedia.home.discord"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 114f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslatableComponent("gui.resourcefulbees.beepedia.home.patreon"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 89f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslatableComponent("gui.resourcefulbees.beepedia.home.issues"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 64f, yPos + 129f, 25, 25, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslatableComponent("gui.resourcefulbees.beepedia.home.wiki"), mouseX, mouseY);
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
}
