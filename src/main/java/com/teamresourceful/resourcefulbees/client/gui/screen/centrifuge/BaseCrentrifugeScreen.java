package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseCrentrifugeScreen<T extends Container> extends ContainerScreen<T> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/background.png");

    protected BaseCrentrifugeScreen(T pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 360;
        this.imageHeight = 228;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mouseAlteredX = (int) (mouseX-leftPos);
        int mouseAlteredY = (int) (mouseY-topPos);
        if (MathUtils.inRangeInclusive(mouseAlteredX, 346, 358) && MathUtils.inRangeInclusive(mouseAlteredY, 3, 15) && button == 0){
            closeScreen();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrix, int pX, int pY) {
        font.draw(matrix, this.title, 21,4, 0xffffff);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float pPartialTicks, int pX, int pY) {
        this.renderBackground(matrix);
        if (minecraft != null) {
            minecraft.textureManager.bind(BACKGROUND);
            AbstractGui.blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight, 360, 228);
        }
    }

    @Override
    protected void renderTooltip(@NotNull MatrixStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);
        int mouseAlteredX = x-leftPos;
        int mouseAlteredY = y-topPos;

        if (MathUtils.inRangeInclusive(mouseAlteredX, 332, 344) && MathUtils.inRangeInclusive(mouseAlteredY, 3, 15)){
            List<ITextComponent> tooltip = getInfoTooltip();
            if (tooltip != null) GuiUtils.drawHoveringText(matrixStack, tooltip, x, y, width, height, width, font);
        }
        if (MathUtils.inRangeInclusive(mouseAlteredX, 346, 358) && MathUtils.inRangeInclusive(mouseAlteredY, 3, 15)){
            GuiUtils.drawHoveringText(matrixStack, Lists.newArrayList(TranslationConstants.Centrifuge.CLOSE), x, y, width, height, width, font);
        }
    }

    @Nullable
    abstract List<ITextComponent> getInfoTooltip();

    abstract void closeScreen();
}
