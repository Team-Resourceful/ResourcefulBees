package com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons.NavButton;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class NavigableControlPanel<T extends AbstractGUICentrifugeEntity> extends AbstractControlPanel {

    protected final Collection<BlockPos> navList;
    protected @Nullable T selectedEntity;
    private Component navLabel;

    protected NavigableControlPanel(int x, int y, CentrifugeTerminalScreen screen, Collection<BlockPos> navList) {
        super(x, y, screen);
        this.navList = navList;
    }

    public void initializeSelection() {
        updateSelectedEntity();
        updateNavLabel();
        addNavButtons();
    }

    private void addNavButtons() {
        if (navList.size() <= 1) return;
        addRenderableWidget(new NavButton(x + 3, y + 3, true, () -> navigate(true)));
        addRenderableWidget(new NavButton(x + 65, y + 3, false, () -> navigate(false)));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(CentrifugeTextures.COMPONENTS, x, y, 0, 193, 75, 63);
        TextUtils.tf12DrawCenteredStringNoShadow(graphics, navLabel, x + 37, y + 6, TextUtils.FONT_COLOR_1);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void navigate(boolean reverse) {
        if (navList.isEmpty()) return;
        screen.rotateSelection(navList.size() - 1, reverse);
        updateSelectedEntity();
        updateNavLabel();
        screen.notifyInfoPanelOfEntitySelection();
    }

    protected abstract void updateSelectedEntity();

    protected abstract Component getNavType();

    private void updateNavLabel() {
        this.navLabel = Component.literal(getNavType().getString() + " #" + (screen.selectionIndex() + 1));
    }

    public AbstractGUICentrifugeEntity selectedEntity() {
        return selectedEntity;
    }
}
