package com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.ViewButton;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.network.packets.client.SwitchGuiPacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInfoPanel<T extends AbstractGUICentrifugeEntity> extends ParentWidget {

    protected @Nullable T selectedEntity;
    protected final boolean displayTitleBar;

    protected AbstractInfoPanel(int x, int y, boolean displayTitleBar) {
        super(x, y);
        this.displayTitleBar = displayTitleBar;
    }

    @Override
    protected void init() {
        if (displayTitleBar) {
            addRenderableWidget(new ViewButton(x+2, y+2, this::viewSelectedEntity));
        }
    }

    private void viewSelectedEntity() {
        if (selectedEntity == null) return;
        NetworkHandler.CHANNEL.sendToServer(new SwitchGuiPacket(selectedEntity.getBlockPos()));
    }

    public abstract void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity);

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        if (displayTitleBar) {
            RenderUtils.bindTexture(CentrifugeTextures.COMPONENTS);
            blit(stack, x+2, y+16, 21, 0, 233, 3);
            TextUtils.tf12DrawCenteredStringNoShadow(stack, selectedEntity.getDisplayName(), x + 126.5f, y + 6.5f, TextUtils.FONT_COLOR_1);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
