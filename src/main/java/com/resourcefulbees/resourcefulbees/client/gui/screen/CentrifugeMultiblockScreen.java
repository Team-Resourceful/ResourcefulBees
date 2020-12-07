package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CentrifugeMultiblockScreen extends CentrifugeScreen {

    public CentrifugeMultiblockScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void initializeData() {
        this.screenWidth = 178;
        inputStartX = 52;
        outputStartX = 43;
        rBdrCor = -2;
    }
}
