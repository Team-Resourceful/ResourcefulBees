package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.FontResourceManagerAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.MinecraftAccessor;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public abstract class BaseCentrifugeScreen<T extends CentrifugeContainer<?>> extends ContainerScreen<T> {

    protected static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/background.png");
    protected static final ResourceLocation COMPONENTS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/components.png");
    protected static final Font FONT_8 =  ((FontResourceManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(ResourcefulBees.MOD_ID, "jetbrains_mono_8"));
    protected static final FontRenderer TERMINAL_FONT_8 = new FontRenderer(resourceLocation -> FONT_8);
    protected static final Font FONT_12 =  ((FontResourceManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(ResourcefulBees.MOD_ID, "jetbrains_mono_12"));
    protected static final FontRenderer TERMINAL_FONT_12 = new FontRenderer(resourceLocation -> FONT_12);

    protected static final Rectangle CLOSE = new Rectangle(345, 2, 13, 13);
    protected static final Rectangle BACK = new Rectangle(2, 2, 13, 13);

    protected final CentrifugeTier tier;
    protected CentrifugeState centrifugeState;

    protected BaseCentrifugeScreen(T pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
        this.imageWidth = 360;
        this.imageHeight = 228;
        this.centrifugeState = pMenu.getEntity() == null  ? new CentrifugeState() : pMenu.getEntity().getCentrifugeState();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mouseAlteredX = (int) (mouseX-leftPos);
        int mouseAlteredY = (int) (mouseY-topPos);
        if (CLOSE.contains(mouseAlteredX, mouseAlteredY) && button == 0){
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
        TERMINAL_FONT_12.draw(matrix, this.title, 10,5, 0xffffff);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float pPartialTicks, int pX, int pY) {
        this.renderBackground(matrix);
        if (minecraft == null) return;
        minecraft.textureManager.bind(BACKGROUND);
        blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight, 360, 228);
        minecraft.textureManager.bind(COMPONENTS);
        drawInfoPane(matrix, leftPos, topPos);
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

    protected void drawInfoPane(@NotNull MatrixStack matrix, int x, int y) {
        blit(matrix, x + 102, y + 39, 19, 0, 237, 164);
    }

    @Nullable
    abstract List<ITextComponent> getInfoTooltip();

    abstract void closeScreen();
}
