package com.teamresourceful.resourcefulbees.centrifuge.client.screens;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.OutputControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.output.TerminalOutputHomePanel;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeItemOutputContainer;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeItemOutputEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CentrifugeItemOutputScreen extends CentrifugeInventoryScreen<CentrifugeItemOutputContainer> {

    public CentrifugeItemOutputScreen(CentrifugeItemOutputContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 18);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new OutputControlPanel(leftPos + 21, topPos + 39, this));
        initializeControlPanelTab();
    }

    @Override
    protected void setNavPanelTab(boolean initialize) {
        if (initialize) {
            switchNavPanelTab(this.navPanelTab, currentInfoPanel);
        } else switch (controlPanelTab) {
            case HOME -> setDefaultNavPanelTab(TerminalPanels.ITEM_OUTPUTS_HOME);
            case INVENTORY -> setDefaultNavPanelTab(TerminalPanels.INVENTORY);
            default -> removeNavPanelIfExists();
        }
    }

    @Override
    protected void updateInfoPanel(@NotNull TerminalPanels newInfoPanel) {
        int pX = leftPos+102;
        int pY = topPos+39;
        switch (newInfoPanel) {
            case INVENTORY -> {
                removeInfoPanelIfExists();
                menu.enableSlots();
            }
            case ITEM_OUTPUTS_HOME -> {
                updateInfoPanel(new TerminalOutputHomePanel<>(pX, pY, CentrifugeItemOutputEntity.class, false));
                menu.disableSlots();
            }
            default -> removeInfoPanelIfExists();
        }
    }
}
