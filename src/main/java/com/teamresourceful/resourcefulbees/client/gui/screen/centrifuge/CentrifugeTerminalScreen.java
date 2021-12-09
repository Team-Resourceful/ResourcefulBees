package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.AbstractTerminalModule;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.DisplayTab;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.terminal.TerminalDumpsModule;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.terminal.TerminalHomeModule;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.terminal.TerminalInputsModule;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.terminal.TerminalOutputsModule;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CentrifugeTerminalScreen extends BaseCentrifugeScreen<CentrifugeTerminalContainer> {

    //TODO despite "abstracting" a lot of this stuff there still seems to be lots of duplicated overrides,
    // method calls, and data that can most likely be generalized even further. figure out how to condense
    // this even further

    private static final Component TERMINAL_TEXT = new TextComponent("Terminal");
    private static final Component HOME_TEXT = new TextComponent("Home");
    private static final Component INPUTS_TEXT = new TextComponent("Inputs");
    private static final Component ITEM_OUTS_TEXT = new TextComponent("Item Outputs");
    private static final Component FLUID_OUTS_TEXT = new TextComponent("Fluid Outputs");
    private static final Component DUMPS_TEXT = new TextComponent("Dumps");

    //Display Tabs
    private final DisplayTab home = new DisplayTab(24, 58, 69, 13, HOME_TEXT, this);
    private final DisplayTab inputs = new DisplayTab(24, 72, 69, 13, INPUTS_TEXT, this);
    private final DisplayTab itemOuts = new DisplayTab(24, 86, 69, 13, ITEM_OUTS_TEXT, this);
    private final DisplayTab fluidOuts = new DisplayTab(24, 100, 69, 13, FLUID_OUTS_TEXT, this);
    private final DisplayTab dumps = new DisplayTab(24, 114, 69, 13, DUMPS_TEXT, this);

    private final TerminalHomeModule homeModule = new TerminalHomeModule(this);
    private final TerminalInputsModule inputsModule = new TerminalInputsModule(this);
    private final TerminalOutputsModule itemOutputsModule = new TerminalOutputsModule(this);
    private final TerminalOutputsModule fluidOutputsModule = new TerminalOutputsModule(this);
    private final TerminalDumpsModule dumpsModule = new TerminalDumpsModule(this);

    private final Map<DisplayTab, AbstractTerminalModule<?>> tabs = new HashMap<>();
    private Map.Entry<DisplayTab, AbstractTerminalModule<?>> loadedModule = Pair.of(home, homeModule);

    public CentrifugeTerminalScreen(CentrifugeTerminalContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        tabs.put(home, homeModule);
        tabs.put(inputs, inputsModule);
        tabs.put(itemOuts, itemOutputsModule);
        tabs.put(fluidOuts, fluidOutputsModule);
        tabs.put(dumps, dumpsModule);
        loadedModule.getKey().setSelected(true);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float pPartialTicks, int pX, int pY) {
        super.renderBg(matrix, pPartialTicks, pX, pY);
        blit(matrix, leftPos + 21, topPos + 39, 75, 165, 75, 91);
        tabs.keySet().forEach(tab -> tab.renderBackground(matrix, pPartialTicks, pX, pY));
        loadedModule.getValue().renderBackground(matrix, pPartialTicks, pX, pY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        super.renderLabels(matrix, mouseX, mouseY);
        RenderUtils.TERMINAL_FONT_12.draw(matrix, TERMINAL_TEXT, 58f - (RenderUtils.TERMINAL_FONT_12.width(TERMINAL_TEXT)/2f),45.5f, RenderUtils.FONT_COLOR_1);
        tabs.keySet().forEach(tab -> tab.renderText(matrix, mouseX, mouseY));
        loadedModule.getValue().renderText(matrix, mouseX, mouseY);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        for (Map.Entry<DisplayTab, AbstractTerminalModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onCharTyped(pCodePoint, pModifiers)) return true;
        }
        return loadedModule.getValue().onCharTyped(pCodePoint, pModifiers) || super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for (Map.Entry<DisplayTab, AbstractTerminalModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onKeyPressed(pKeyCode, pScanCode, pModifiers)) return true;
        }
        return loadedModule.getValue().onKeyPressed(pKeyCode, pScanCode, pModifiers) || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (Map.Entry<DisplayTab, AbstractTerminalModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onMouseScrolled(pMouseX, pMouseY, pDelta)) return true;
        }
        return loadedModule.getValue().onMouseScrolled(pMouseX, pMouseY, pDelta) || super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    @Nullable List<Component> getInfoTooltip() {
        return Lists.newArrayList(new TextComponent("INFO TEXT"));
    }

    @Override
    void closeScreen() {
        if (minecraft != null) minecraft.setScreen(null);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Map.Entry<DisplayTab, AbstractTerminalModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onMouseClicked(mouseX, mouseY, button)) {
                loadedModule.getKey().setSelected(false);
                loadedModule = entry;
                loadedModule.getKey().setSelected(true);
                return true;
            }
        }
        return loadedModule.getValue().onMouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    public void sendResponse(Component response) {
        loadedModule.getValue().onTerminalResponse(response);
    }
}
