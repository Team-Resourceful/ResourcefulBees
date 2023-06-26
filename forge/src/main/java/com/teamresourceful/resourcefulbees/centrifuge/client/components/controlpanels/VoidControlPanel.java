package com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels;

import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeVoidScreen;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class VoidControlPanel extends AbstractControlPanel {

    public VoidControlPanel(int x, int y, CentrifugeVoidScreen screen) {
        super(x, y, screen);
    }

    @Override
    protected void init() {
        createControlPanelDisplayTab(y + 18, ControlPanelTabs.HOME);
        createControlPanelDisplayTab(y + 32, ControlPanelTabs.INVENTORY);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(CentrifugeTextures.COMPONENTS, x, y, 75, 165, 75, 91);
        TextUtils.tf12DrawCenteredStringNoShadow(graphics, CentrifugeTranslations.TERMINAL_TAB, x + 37, y + 6, TextUtils.FONT_COLOR_1);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
