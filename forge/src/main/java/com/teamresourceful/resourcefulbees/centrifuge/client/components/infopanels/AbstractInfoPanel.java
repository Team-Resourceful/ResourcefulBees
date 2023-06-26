package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons.ViewButton;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.SwitchGuiPacket;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;
import net.minecraft.client.gui.GuiGraphics;
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
            addRenderableWidget(new ViewButton(x + 2, y + 2, this::viewSelectedEntity));
        }
    }

    private void viewSelectedEntity() {
        if (selectedEntity == null) return;
        NetworkHandler.CHANNEL.sendToServer(new SwitchGuiPacket(selectedEntity.getBlockPos()));
    }

    public abstract void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity);

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        if (displayTitleBar) {
            graphics.blit(CentrifugeTextures.COMPONENTS, x + 2, y + 16, 21, 0, 233, 3);
            TextUtils.tf12DrawCenteredStringNoShadow(graphics, selectedEntity.getDisplayName(), x + 126, y + 6, TextUtils.FONT_COLOR_1);
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
