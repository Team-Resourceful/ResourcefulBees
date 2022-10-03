package com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.NavButton;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class NavigableControlPanel<T extends AbstractGUICentrifugeEntity> extends AbstractControlPanel {

    protected final Collection<BlockPos> navList;
    protected @Nullable T selectedEntity;
    private final String navType;
    private Component navLabel;

    protected NavigableControlPanel(int x, int y, CentrifugeTerminalScreen screen, Collection<BlockPos> navList, String navType) {
        super(x, y, screen);
        this.navList = navList;
        this.navType = navType;
        updateSelectedEntity();
        updateNavLabel();
        addNavButtons();
    }

    private void addNavButtons() {
        if (navList.size() <= 1) return;
        addRenderableWidget(new NavButton(x+3, y+3, true, () -> navigate(true)));
        addRenderableWidget(new NavButton(x+65, y+3, false, () -> navigate(false)));
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        ClientUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x, y, 0, 193, 75, 63);
        TextUtils.tf12DrawCenteredStringNoShadow(stack, navLabel, x+37f, y+6.5f, TextUtils.FONT_COLOR_1);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void navigate(boolean reverse) {
        if (navList.isEmpty()) return;
        screen.rotateSelection(navList.size() - 1, reverse);
        updateSelectedEntity();
        updateNavLabel();
        screen.notifyInfoPanelOfEntitySelection();
    }

    protected abstract void updateSelectedEntity();

    private void updateNavLabel() {
        this.navLabel = Component.literal(navType + " #" + (screen.selectionIndex() + 1));
    }

    public AbstractGUICentrifugeEntity selectedEntity() {
        return selectedEntity;
    }
}