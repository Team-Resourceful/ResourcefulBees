package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ModImageButton;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class HomePage extends BeepediaPage {

    private final ResourceLocation discordButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/discord.png");
    private final ResourceLocation patreonButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/patreon.png");
    private final ResourceLocation wikiButton = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/wiki.png");


    int counter = 0;
    List<BeePage> bees;
    ModImageButton discord;
    ModImageButton patreon;
    ModImageButton wiki;

    ResourceLocation logo = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/logo.png");

    public HomePage(BeepediaScreen beepedia, int left, int top) {
        super(beepedia, left, top, "home");
        bees = new LinkedList<>();
        beepedia.bees.forEach((s, b) -> bees.add(b));
        discord = new ModImageButton(xPos + 148, yPos + 138, 16, 16, 0, 0, 16, discordButton, onPress -> {
            Util.getOSType().openURI("https://discord.resourcefulbees.com");
        });
        patreon = new ModImageButton(xPos + 132, yPos + 138, 16, 16, 0, 0, 16, patreonButton, onPress -> {
            Util.getOSType().openURI("https://patreon.resourcefulbees.com");
        });
        wiki = new ModImageButton(xPos + 116, yPos + 138, 16, 16, 0, 0, 16, wikiButton, onPress -> {
            Util.getOSType().openURI("https://wiki.resourcefulbees.com");
        });
        beepedia.addButton(discord);
        discord.visible = false;
        beepedia.addButton(patreon);
        patreon.visible = false;
        beepedia.addButton(wiki);
        wiki.visible = false;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScaleFactor();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getFramebufferHeight() - (this.yPos + 80) * scale);
        GL11.glScissor((int) (this.xPos * scale), scissorY, (int) (SUB_PAGE_WIDTH * scale), (int) ((73) * scale));
        RenderUtils.renderEntity(matrix, bees.get(counter).getBee(), beepedia.getMinecraft().world, xPos + (SUB_PAGE_WIDTH / 2F) - 12F, yPos + 10f, -45, 3);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        FontRenderer font = Minecraft.getInstance().fontRenderer;
        font.draw(matrix, new TranslationTextComponent("itemGroup.resourcefulbees").formatted(TextFormatting.GRAY), xPos + 30F, yPos + 81F, -1);
        Minecraft.getInstance().getTextureManager().bindTexture(logo);
        AbstractGui.drawTexture(matrix, xPos + (SUB_PAGE_WIDTH / 2) - 54, yPos + 90, 0, 0, 104, 16, 104, 16);
    }

    @Override
    public String getSearch() {
        return new TranslationTextComponent("gui.resourcefulbees.beepedia.home_button").getString();
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            counter++;
            if (counter >= bees.size()) counter = 0;
            BeepediaScreen.currScreenState.setHomeCounter(counter);
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (BeepediaScreen.mouseHovering(xPos + 148, yPos + 138, 16, 16, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.discord"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 132, yPos + 138, 16, 16, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.patreon"), mouseX, mouseY);
        }
        if (BeepediaScreen.mouseHovering(xPos + 116, yPos + 138, 16, 16, mouseX, mouseY)) {
            beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.beepedia.home.wiki"), mouseX, mouseY);
        }
    }

    @Override
    public void openPage() {
        super.openPage();
        discord.visible = true;
        patreon.visible = true;
        wiki.visible = true;
        counter = BeepediaScreen.currScreenState.getHomeCounter();
    }

    @Override
    public void closePage() {
        super.closePage();
        discord.visible = false;
        patreon.visible = false;
        wiki.visible = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }
}
