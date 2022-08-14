package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public abstract class BaseCentrifugeScreen<T extends CentrifugeContainer<?>> extends AbstractContainerScreen<T> {

    protected static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/background.png");
    protected static final ResourceLocation COMPONENTS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/components.png");

    protected static final Rectangle CLOSE = new Rectangle(345, 2, 13, 13); //TODO replace Rectangle Class
    protected static final Rectangle BACK = new Rectangle(2, 2, 13, 13);

    protected final CentrifugeTier tier;
    protected CentrifugeState centrifugeState;

    protected BaseCentrifugeScreen(T pMenu, Inventory pPlayerInventory, net.minecraft.network.chat.Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
        this.imageWidth = 360;
        this.imageHeight = 228;
        this.centrifugeState = pMenu.getCentrifugeState();
    }

    public CentrifugeState getCentrifugeState() {
        return centrifugeState;
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
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int pX, int pY) {
        ClientUtils.TERMINAL_FONT_12.draw(matrix, this.title, 10,5, 0xffc9c9c9);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float pPartialTicks, int pX, int pY) {
        this.renderBackground(matrix);
        if (minecraft == null) return;
        //BACKGROUND
        ClientUtils.bindTexture(BACKGROUND);
        blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight, 360, 228);
        //COMPONENTS
        ClientUtils.bindTexture(COMPONENTS);
        drawInfoPane(matrix, leftPos, topPos);
    }

    @Override
    protected void renderTooltip(@NotNull PoseStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);
        int mouseAlteredX = x-leftPos;
        int mouseAlteredY = y-topPos;

        if (MathUtils.inRangeInclusive(mouseAlteredX, 332, 344) && MathUtils.inRangeInclusive(mouseAlteredY, 3, 15)){
            List<Component> tooltip = getInfoTooltip();
            if (tooltip != null) this.renderTooltip(matrixStack, tooltip, Optional.empty(), x, y, font);
        }
        if (MathUtils.inRangeInclusive(mouseAlteredX, 346, 358) && MathUtils.inRangeInclusive(mouseAlteredY, 3, 15)){
            //TODO GuiUtils.drawHoveringText(matrixStack, Lists.newArrayList(TranslationConstants.Centrifuge.CLOSE), x, y, width, height, width, font);
        }
    }

    protected void drawInfoPane(@NotNull PoseStack matrix, int x, int y) {
        blit(matrix, x + 102, y + 39, 19, 0, 237, 164);
    }

    @Nullable
    abstract List<Component> getInfoTooltip();

    abstract void closeScreen();
}
