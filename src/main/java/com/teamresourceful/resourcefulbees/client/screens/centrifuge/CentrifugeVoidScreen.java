package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeVoidContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeVoidScreen extends CentrifugeInventoryScreen<CentrifugeVoidContainer> {

    public CentrifugeVoidScreen(CentrifugeVoidContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 54);
    }

    @Override
    protected void switchControlPanelTab(ControlPanelTabs controlPanelTab, boolean initialize) {

    }

    @Override
    protected void setNavPanelTab(boolean initialize) {

    }

    @Override
    protected void updateInfoPanel(@NotNull TerminalPanels newInfoPanel) {

    }

    @Override
    protected void drawContainerSlots(@NotNull PoseStack matrix, int x, int y) {
        drawSlotGrid(matrix, x + 161, y + 45, tier.getContainerRows(), tier.getContainerColumns() * 2, 0, 54);
    }
}
