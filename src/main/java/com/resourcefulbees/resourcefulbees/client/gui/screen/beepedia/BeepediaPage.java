package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ButtonList;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ListButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public abstract class BeepediaPage {

    public final int yPos;
    public final int xPos;
    public final BeepediaScreen beepedia;

    public ListButton listButton = null;

    public final ResourceLocation arrowImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/arrows.png");
    public final ResourceLocation listImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");
    public final String id;

    protected BeepediaPage(BeepediaScreen beepedia, int xPos, int yPos, String id) {
        this.beepedia = beepedia;
        this.yPos = yPos;
        this.xPos = xPos;
        this.id = id;
    }

    public void updateListPosition(int xPos, int yPos) {
        if (listButton == null) return;
        listButton.x = xPos;
        listButton.y = yPos;
    }

    public void newListButton(ItemStack item, TextComponent text) {
        listButton = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, item, 2, 2, text, 22, 6, onPress -> beepedia.setActive(this));
    }

    public void openPage() {
        if (listButton == null) return;
        listButton.active = false;
    }

    public void closePage() {
        if (listButton == null) return;
        listButton.active = true;
    }

    public abstract void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY);

    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        // override to implement
    }

    public abstract String getSearch();

    public void tick(int ticksActive) {
        // override to implement
    }

    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        // override to implement
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        // override to implement
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        // override to implement
        return false;
    }


}
