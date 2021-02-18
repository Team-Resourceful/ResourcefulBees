package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.container.ApiaryStorageContainer;
import com.resourcefulbees.resourcefulbees.container.BeepediaContainer;
import com.resourcefulbees.resourcefulbees.tileentity.AcceleratorTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public class BeepediaScreen extends ContainerScreen<BeepediaContainer> {

    public BeepediaScreen(BeepediaContainer beepediaContainer, PlayerInventory inventory, ITextComponent name) {
        super(beepediaContainer, inventory, name);
    }

    @Override
    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }
}
