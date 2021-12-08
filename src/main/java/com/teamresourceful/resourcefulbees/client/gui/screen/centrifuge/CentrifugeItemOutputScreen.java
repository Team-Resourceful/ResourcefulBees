package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeItemOutputContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CentrifugeItemOutputScreen extends CentrifugeInventoryScreen<CentrifugeItemOutputContainer> {

    public CentrifugeItemOutputScreen(CentrifugeItemOutputContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 18);
    }
}
