package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeItemOutputContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CentrifugeItemOutputScreen extends CentrifugeInventoryScreen<CentrifugeItemOutputContainer> {

    public CentrifugeItemOutputScreen(CentrifugeItemOutputContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 0 , 18);
    }
}
