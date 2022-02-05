package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeVoidContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeVoidScreen extends CentrifugeInventoryScreen<CentrifugeVoidContainer> {

    public CentrifugeVoidScreen(CentrifugeVoidContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 54);
    }

    @Override
    protected void drawContainerSlots(@NotNull PoseStack matrix, int x, int y) {
        drawSlotGrid(matrix, x + 161, y + 45, CentrifugeUtils.getRows(tier), CentrifugeUtils.getColumns(tier) * 2, 0, 54);
    }
}
