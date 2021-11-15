package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputScreen extends CentrifugeInventoryScreen<CentrifugeInputContainer> {

    public CentrifugeInputScreen(CentrifugeInputContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0, 36);
    }

    @Override
    protected void drawContainerSlots(@NotNull MatrixStack matrix, int x, int y) {
        drawSlot(matrix, x + 125, y + 63, 0, 0);
        super.drawContainerSlots(matrix, x, y);
    }
}
