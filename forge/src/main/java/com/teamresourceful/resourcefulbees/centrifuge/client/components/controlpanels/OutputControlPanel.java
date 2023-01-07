package com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.BaseCentrifugeScreen;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class OutputControlPanel extends AbstractControlPanel {

    public OutputControlPanel(int x, int y, BaseCentrifugeScreen<?> screen) {
        super(x, y, screen);
    }

    @Override
    protected void init() {
        createControlPanelDisplayTab(y+18, ControlPanelTabs.HOME);
        createControlPanelDisplayTab(y+32, ControlPanelTabs.INVENTORY);
        createNavPanelVoidExcessTab(y+60, ((AbstractCentrifugeOutputEntity<?, ?>) screen.getMenu().getEntity())::voidsExcess);
        createNavPanelPurgeTab(y+74);
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x, y, 75, 165, 75, 91);
        TextUtils.tf12DrawCenteredStringNoShadow(stack, Component.literal("Terminal"), x+37f, y+6.5f, TextUtils.FONT_COLOR_1);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
