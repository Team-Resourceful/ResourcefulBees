package com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal.dump;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import org.jetbrains.annotations.NotNull;

public class TerminalVoidHomePanel extends AbstractInfoPanel<CentrifugeVoidEntity> {

    public TerminalVoidHomePanel(int x, int y) {
        super(x, y);
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        this.selectedEntity = (CentrifugeVoidEntity) selectedEntity;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        ClientUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x+2, y+16, 21, 0, 233, 3);
        TextUtils.tf12DrawCenteredStringNoShadow(stack, selectedEntity.getDisplayName(), x+118.5f, y+6.5f, TextUtils.FONT_COLOR_1);
        int tX = x+14;
        int tY = y+14;

        drawLocationString(stack, CentrifugeUtils.formatBlockPos(selectedEntity.getBlockPos()), tX+6, tY+16);
    }

    //TODO make these translatable texts
    private static void drawLocationString(PoseStack stack, String location, int x, int y) {
        drawString(stack, "Location: " + location, x, y);
    }

    private static void drawString(PoseStack stack, String string, int x, int y) {
        TextUtils.TERMINAL_FONT_8.draw(stack, string, x, y, TextUtils.FONT_COLOR_1);
    }
}
