package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeVoidContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class CentrifugeVoidScreen extends CentrifugeInventoryScreen<CentrifugeVoidContainer> {

    public CentrifugeVoidScreen(CentrifugeVoidContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 54);
    }

    protected void drawContainerSlots(@NotNull MatrixStack matrix, int x, int y) {
        drawSlotGrid(matrix, x + 161, y + 45, CentrifugeUtils.getRows(tier), CentrifugeUtils.getColumns(tier) * 2, 0, 54);
    }
}
