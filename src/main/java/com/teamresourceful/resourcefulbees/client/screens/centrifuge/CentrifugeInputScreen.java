package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeInputScreen extends CentrifugeInventoryScreen<CentrifugeInputContainer> {

    public CentrifugeInputScreen(CentrifugeInputContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0, 36);
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
        drawSlot(matrix, x + 125, y + 63, 0, 0);
        super.drawContainerSlots(matrix, x, y);
    }
}
