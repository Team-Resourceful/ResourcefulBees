package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputScreen extends ContainerScreen<CentrifugeInputContainer> {

    public static final ResourceLocation SLOT_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/slot_bg.png");
    private final CentrifugeTier tier;


    public CentrifugeInputScreen(CentrifugeInputContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bind(SLOT_BG);
            int i = this.leftPos;
            int j = this.topPos;

            blit(matrix, i + 9, j + 9, 0, 0, 18, 18);

            int rows = tier.equals(CentrifugeTier.BASIC) ? 1 : tier.getSlots() / 4;
            int columns = tier.equals(CentrifugeTier.BASIC) ? 1 : 4;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    blit(matrix, i + 29 + c * 18, j + 9 + r * 18, 0, 0, 18, 18);
                }
            }

            int xOffset = this.menu.getInvOffsetX() -1;
            int yOffset = this.menu.getInvOffsetY() -1;


            for (int r = 0; r < 3; ++r) {
                for (int c = 0; c < 9; ++c) {
                    blit(matrix, i + xOffset + c * 18, j + yOffset + r * 18, 0, 0, 18, 18);
                }
            }

            yOffset += 58;

            for (int k = 0; k < 9; ++k) {
                blit(matrix, i + xOffset + k * 18, j + yOffset, 0, 0, 18, 18);
            }
        }
    }

    @Override
    protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY) {
        //do nothing
    }
}
