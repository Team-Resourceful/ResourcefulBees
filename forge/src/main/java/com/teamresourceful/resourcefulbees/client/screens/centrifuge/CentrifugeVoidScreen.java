package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.VoidControlPanel;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal.dump.TerminalVoidHomePanel;
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
    protected void init() {
        super.init();
        addRenderableWidget(new VoidControlPanel(leftPos+21, topPos+39, this));
        initializeControlPanelTab();
    }

    @Override
    protected void setNavPanelTab(boolean initialize) {
        if (initialize) {
            switchNavPanelTab(this.navPanelTab, currentInfoPanel);
        } else switch (controlPanelTab) {
            case HOME -> setDefaultNavPanelTab(TerminalPanels.DUMPS_HOME);
            case INVENTORY -> setDefaultNavPanelTab(TerminalPanels.INVENTORY);
            default -> removeNavPanelIfExists();
        }
    }

    @Override
    protected void updateInfoPanel(@NotNull TerminalPanels newInfoPanel) {
        int pX = leftPos+102;
        int pY = topPos+39;
        switch (newInfoPanel) {
            //Terminal_Home does not change the nav stuff or update/reset the selection index
            case INVENTORY -> {
                removeInfoPanelIfExists();
                menu.enableSlots();
            }
            case DUMPS_HOME -> {
                updateInfoPanel(new TerminalVoidHomePanel(pX, pY, false));
                menu.disableSlots();
            }
            default -> removeInfoPanelIfExists();
        }
    }

    @Override
    protected void drawContainerSlots(@NotNull PoseStack matrix, int x, int y) {
        drawSlotGrid(matrix, x + 160, y + 45, tier.getContainerRows(), tier.getContainerColumns() * 2, u, v);
    }
}
