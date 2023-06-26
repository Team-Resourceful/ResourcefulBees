package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.TerminalCommandHandler;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

import static com.teamresourceful.resourcefulbees.client.util.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.util.TextUtils.TERMINAL_FONT_8;


public class TerminalHomePanel extends AbstractInfoPanel<CentrifugeTerminalEntity> {

    private final TerminalCommandHandler commandHandler;
    private final CentrifugeState centrifugeState; //this should probably not be final and instead updatable
    private final int tX;
    private final CentrifugeEnergyStorage energyStorage;

    public TerminalHomePanel(int x, int y, CentrifugeState centrifugeState, CentrifugeTerminalEntity terminal, CentrifugeEnergyStorage energyStorage) {
        super(x, y, false);
        this.centrifugeState = centrifugeState;
        this.selectedEntity = terminal;
        this.energyStorage = energyStorage;
        this.tX = x+10;
        this.commandHandler = new TerminalCommandHandler(terminal);
        init();
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        // only one terminal allowed per multiblock
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.commandHandler.isNeofetch()) {
            drawASCII(graphics);
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(tX + 90d, y + 20d, 0);
                String owner = centrifugeState.getOwner() + "@centrifuge";
                drawComponent(graphics, owner, 0);
                drawComponent(graphics, StringUtils.repeat('─', owner.length()), 8);
                drawComponent(graphics, CentrifugeTranslations.MAX_TIER, StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 16);
                drawComponent(graphics, CentrifugeTranslations.ENERGY, energyStorage.getStored(), energyStorage.getCapacity(), 24);
                drawComponent(graphics, CentrifugeTranslations.INPUTS, centrifugeState.getInputs().size(), 32);
                drawComponent(graphics, CentrifugeTranslations.ITEM_OUTPUTS, centrifugeState.getItemOutputs().size(), 40);
                drawComponent(graphics, CentrifugeTranslations.VOIDS, centrifugeState.getFilters().size(), 56);
                drawComponent(graphics, CentrifugeTranslations.FLUID_OUTPUTS, centrifugeState.getFluidOutputs().size(), 48);
                drawComponent(graphics, CentrifugeTranslations.ENERGY_PORTS, centrifugeState.getEnergyPorts(), 64);
                drawComponent(graphics, CentrifugeTranslations.GEARBOXES, centrifugeState.getGearboxes(), 72);
                drawComponent(graphics, CentrifugeTranslations.PROCESSORS, centrifugeState.getProcessors(), 80);
                drawComponent(graphics, CentrifugeTranslations.RECIPE_POWER, formatAsPercent(centrifugeState.getRecipePowerModifier()), 88);
                drawComponent(graphics, CentrifugeTranslations.RECIPE_TIME, formatAsPercent(centrifugeState.getRecipeTimeModifier()), 96);
            }
            this.commandHandler.drawInput(graphics, tX + 4, y + 135);
        } else {
            this.commandHandler.render(graphics, tX, y);
        }
    }

    @Override
    public boolean charTyped(char typedChar, int modifiers) {
        this.commandHandler.charTyped(typedChar);
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        if (!this.commandHandler.keyPressed(key)) {
            return super.keyPressed(key, scan, modifiers);
        }
        return true;
    }

    private void drawASCII(@NotNull GuiGraphics graphics) {
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(tX - 10d, y + 20d, 0);
            pose.scale(0.8f, 0.8f, 0.8f);
            drawComponent(graphics, "     ^^      .-=-=-=-.  ^^", 24);
            drawComponent(graphics, " ^^        (`-=-=-=-=-`)", 32);
            drawComponent(graphics, "         (`-=-=-=-=-=-=-`)  ^^", 40);
            drawComponent(graphics, "   ^^   (`-=-=-=-=-=-=-=-`)   ^^", 48);
            drawComponent(graphics, "       (`-=-=-=-=(@)=-=-=-`)", 56);
            drawComponent(graphics, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 64);
            drawComponent(graphics, "       (`-=-=-=-=-=-=-=-=-`)", 72);
            drawComponent(graphics, "       (`-=-=-=-=-=-=-=-=-`)", 80);
            drawComponent(graphics, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 88);
            drawComponent(graphics, "        (`-=-=-=-=-=-=-=-`)", 96);
            drawComponent(graphics, "         (`-=-=-=-=-=-=-`)  ^^", 104);
            drawComponent(graphics, "           (`-=-=-=-=-`)", 112);
            drawComponent(graphics, "            `-=-=-=-=-`", 120);
        }
    }

    public void addResponse(Component response) {
        this.commandHandler.onResponse(response);
    }

    private static String formatAsPercent(double value) {
        return NumberFormat.getPercentInstance().format(value);
    }

    private static void drawComponent(GuiGraphics graphics, String translation, int arg, int y) {
        drawComponent(graphics, Component.translatable(translation, arg), y);
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawComponent(GuiGraphics graphics, String translation, int arg, int arg2, int y) {
        drawComponent(graphics, Component.translatable(translation, arg, arg2), y);
    }

    private static void drawComponent(GuiGraphics graphics, String translation, String arg, int y) {
        drawComponent(graphics, Component.translatable(translation, arg), y);
    }

    private static void drawComponent(GuiGraphics graphics, Component component, int y) {
        graphics.drawString(TERMINAL_FONT_8, component, 6, y, FONT_COLOR_1, false);
    }

    private static void drawComponent(GuiGraphics graphics, String component, int y) {
        graphics.drawString(TERMINAL_FONT_8, component, 6, y, FONT_COLOR_1, false);
    }
}
