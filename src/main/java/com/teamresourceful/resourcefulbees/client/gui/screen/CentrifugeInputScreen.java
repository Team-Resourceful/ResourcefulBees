package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputScreen extends ContainerScreen<CentrifugeInputContainer> {

    public CentrifugeInputScreen(CentrifugeInputContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }



    @Override
    protected void renderBg(@NotNull MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
    }
}
