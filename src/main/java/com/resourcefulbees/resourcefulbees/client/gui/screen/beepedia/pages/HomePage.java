package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class HomePage extends BeepediaPage {

    int counter = 0;
    List<BeePage> bees;

    ResourceLocation logo = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/logo.png");

    public HomePage(BeepediaScreen beepedia, int left, int top) {
        super(beepedia, left, top, "home");
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
        font.draw(matrix, new TranslationTextComponent("itemGroup.resourcefulbees").withStyle(TextFormatting.GRAY), xPos + 30F, yPos + 81F, -1);
        Minecraft.getInstance().getTextureManager().bind(logo);
        AbstractGui.blit(matrix, xPos + (SUB_PAGE_WIDTH / 2) - 54, yPos + 90, 0, 0, 104, 16, 104, 16);
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
    public void openPage() {
        super.openPage();
        counter = BeepediaScreen.currScreenState.getHomeCounter();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }
}
