package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.BackButton;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeContainer;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class CentrifugeInventoryScreen<T extends CentrifugeContainer<?>> extends BaseCentrifugeScreen<T> {

    protected final int u; //this could probably get removed since the value is always zero unless components.png gets changed
    protected final int v;

    protected CentrifugeInventoryScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, int u, int v) {
        super(pMenu, pPlayerInventory, pTitle);
        this.u = u;
        this.v = v;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new BackButton(leftPos+2, topPos+2 , switchGui(BlockPos.of(centrifugeState.getTerminal()))));
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float pPartialTicks, int pX, int pY) {
        super.renderBg(matrix, pPartialTicks, pX, pY);
        if (menu.displaySlots()) {
            drawContainerSlots(matrix, leftPos, topPos);
            drawPlayerInventory(matrix, leftPos - 1 + menu.getPlayerInvXOffset(), topPos - 1 + menu.getPlayerInvYOffset());
        }
    }

    protected void drawContainerSlots(@NotNull PoseStack matrix, int x, int y) {
        drawSlotGrid(matrix, x + 160, y + 45, tier.getContainerRows(), tier.getContainerColumns(), u, v);
    }

    protected void drawPlayerInventory(@NotNull PoseStack matrix, int x, int y) {
        RenderUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        // player inventory
        drawSlotGrid(matrix, x, y, 3, 9, 0, 72);
        //hotbar slots
        drawSlotGrid(matrix, x, y + 55, 1, 9, 0, 72);
    }

    protected void drawSlotGrid(PoseStack matrix, int x, int y, int rows, int columns, int u, int v) {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                drawSlot(matrix, x + c * 17, y + r * 17, u, v);
            }
        }
    }

    protected void drawSlot(PoseStack matrix, int x, int y, int u, int v) {
        blit(matrix, x, y, u, v, 18, 18);
    }

    @Override
    protected void switchControlPanelTab(ControlPanelTabs controlPanelTab, boolean initialize) {
        this.controlPanelTab = controlPanelTab;
        setNavPanelAndUpdate(null, initialize);
    }

    @Override
    public void notifyInfoPanelOfEntitySelection() {
        if (infoPanel == null) return;
        infoPanel.updateSelectedEntity(menu.getEntity());
    }

    @Override
    protected final ControlPanelTabs defaultControlPanelTab() {
        return ControlPanelTabs.INVENTORY;
    }

    @Override
    protected final ControlPanelTabs defaultNavPanelTab() {
        return ControlPanelTabs.INVENTORY;
    }

    @Override
    protected final TerminalPanels defaultInfoPanelTab() {
        return TerminalPanels.INVENTORY;
    }
}
