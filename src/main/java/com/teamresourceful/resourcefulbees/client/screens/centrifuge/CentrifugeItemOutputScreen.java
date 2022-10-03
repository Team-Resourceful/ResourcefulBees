package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeItemOutputContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeItemOutputScreen extends CentrifugeInventoryScreen<CentrifugeItemOutputContainer> {

    public CentrifugeItemOutputScreen(CentrifugeItemOutputContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 18);
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
}
