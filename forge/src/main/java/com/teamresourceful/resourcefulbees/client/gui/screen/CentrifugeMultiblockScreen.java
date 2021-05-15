package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.teamresourceful.resourcefulbees.container.CentrifugeContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CentrifugeMultiblockScreen extends CentrifugeScreen {

    public CentrifugeMultiblockScreen(CentrifugeContainer screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void initializeData() {
        inputStartX = 52;
        outputStartX = 43;
        rBdrCor = -2;
    }
}
