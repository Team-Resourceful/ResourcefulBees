package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.BaseCentrifugeScreen;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeOutputEntity;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class OutputControlPanel extends AbstractControlPanel {

    public OutputControlPanel(int x, int y, BaseCentrifugeScreen<?> screen) {
        super(x, y, screen);
    }

    @Override
    protected void init() {
        createControlPanelDisplayTab(y+18, ControlPanelTabs.INVENTORY);
        createControlPanelDisplayTab(y+32, ControlPanelTabs.HOME);
        createNavPanelVoidExcessTab(y+46, ((AbstractCentrifugeOutputEntity<?, ?>) screen.getMenu().getEntity())::voidsExcess);
        createNavPanelPurgeTab(y+60);
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        ClientUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x, y, 75, 165, 75, 91);
        TextUtils.tf12DrawCenteredStringNoShadow(stack, Component.literal("Terminal"), x+37f, y+6.5f, TextUtils.FONT_COLOR_1);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
