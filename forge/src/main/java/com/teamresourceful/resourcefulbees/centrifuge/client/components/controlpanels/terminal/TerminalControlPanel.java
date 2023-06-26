package com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.terminal;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.AbstractControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class TerminalControlPanel extends AbstractControlPanel {

    public TerminalControlPanel(int x, int y, CentrifugeTerminalScreen terminalScreen) {
        super(x, y, terminalScreen);
    }

    @Override
    protected void init() {
        createControlPanelDisplayTab(y + 18, ControlPanelTabs.HOME);
        createControlPanelDisplayTab(y + 32, ControlPanelTabs.INPUTS);
        createControlPanelDisplayTab(y + 46, ControlPanelTabs.ITEM_OUTPUTS);
        createControlPanelDisplayTab(y + 60, ControlPanelTabs.FLUID_OUTPUTS);
        createControlPanelDisplayTab(y + 74, ControlPanelTabs.FILTERS);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(CentrifugeTextures.COMPONENTS, x, y, 75, 165, 75, 91);
        TextUtils.tf12DrawCenteredStringNoShadow(graphics, CentrifugeTranslations.TERMINAL_TAB, x + 37, y + 6, TextUtils.FONT_COLOR_1);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
