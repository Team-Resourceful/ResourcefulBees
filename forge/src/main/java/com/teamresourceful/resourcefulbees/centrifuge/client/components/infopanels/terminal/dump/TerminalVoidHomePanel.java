package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.dump;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeVoidEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TerminalVoidHomePanel extends AbstractInfoPanel<CentrifugeVoidEntity> {

    public TerminalVoidHomePanel(int x, int y, boolean displayTitleBar) {
        super(x, y, displayTitleBar);
        init();
    }

    public TerminalVoidHomePanel(int x, int y) {
        this(x, y, true);
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        this.selectedEntity = (CentrifugeVoidEntity) selectedEntity;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) {
            drawNoDumpsString(stack, x+118.5f, y+84.5f);
            return;
        }
        super.render(stack, mouseX, mouseY, partialTicks);
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

    private static void drawNoDumpsString(PoseStack stack, float x, float y) {
        TextUtils.tf12DrawCenteredStringNoShadow(stack, Component.literal("Centrifuge has no Void Blocks!"), x, y, 0xffc72c2c);
    }
}
