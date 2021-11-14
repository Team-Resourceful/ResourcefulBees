package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class CentrifugeTerminalScreen extends ContainerScreen<CentrifugeTerminalContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/background.png");
    private final CentrifugeTier tier;
    private final CentrifugeState initialState;

    //TODO we need to synchronize the inputs/outputs lists to the client somehow so that players can assign the
    // links between them. I'm not sure yet on how we want to handle this considering the complexity of the data
    public CentrifugeTerminalScreen(CentrifugeTerminalContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
        this.initialState = (CentrifugeState) pMenu.getGuiPacket();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
        fill(pMatrixStack, 0, 0, width, height, 0xFF0000);
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack pMatrixStack, int pX, int pY) {
        //do nothing
    }


}
