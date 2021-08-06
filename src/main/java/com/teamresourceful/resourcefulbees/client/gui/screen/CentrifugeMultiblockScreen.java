package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.teamresourceful.resourcefulbees.common.container.CentrifugeContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CentrifugeMultiblockScreen extends CentrifugeScreen {

    public CentrifugeMultiblockScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void initializeData() {
        inputStartX = 52;
        outputStartX = 43;
        rBdrCor = -2;
    }
}
