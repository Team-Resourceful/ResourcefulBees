package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.TerminalCommandHandler;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
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
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.commandHandler.isNeofetch()) {
            drawASCII(stack);
            stack.pushPose();
            stack.translate(tX + 90d, y+20d, 0);
            String owner = centrifugeState.getOwner()+"@centrifuge";
            drawComponent(stack, owner, 0);
            drawComponent(stack, StringUtils.repeat('─', owner.length()), 8);
            drawComponent(stack, CentrifugeTranslations.MAX_TIER, StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 16);
            drawComponent(stack, CentrifugeTranslations.ENERGY, energyStorage.getStored(), energyStorage.getCapacity(), 24);
            drawComponent(stack, CentrifugeTranslations.INPUTS, centrifugeState.getInputs().size(), 32);
            drawComponent(stack, CentrifugeTranslations.ITEM_OUTPUTS, centrifugeState.getItemOutputs().size(), 40);
            drawComponent(stack, CentrifugeTranslations.VOIDS, centrifugeState.getFilters().size(), 56);
            drawComponent(stack, CentrifugeTranslations.FLUID_OUTPUTS, centrifugeState.getFluidOutputs().size(), 48);
            drawComponent(stack, CentrifugeTranslations.ENERGY_PORTS, centrifugeState.getEnergyPorts(), 64);
            drawComponent(stack, CentrifugeTranslations.GEARBOXES, centrifugeState.getGearboxes(), 72);
            drawComponent(stack, CentrifugeTranslations.PROCESSORS, centrifugeState.getProcessors(), 80);
            drawComponent(stack, CentrifugeTranslations.RECIPE_POWER, formatAsPercent(centrifugeState.getRecipePowerModifier()), 88);
            drawComponent(stack, CentrifugeTranslations.RECIPE_TIME, formatAsPercent(centrifugeState.getRecipeTimeModifier()), 96);
            stack.popPose();
            this.commandHandler.drawInput(stack, tX + 4f, y + 135f);
        } else {
            this.commandHandler.render(stack, tX, y);
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

    private void drawASCII(@NotNull PoseStack stack) {
        stack.pushPose();
        stack.translate(tX - 10d, y+20d, 0);
        stack.scale(0.8f, 0.8f, 0.8f);
        drawComponent(stack, "     ^^      .-=-=-=-.  ^^", 24);
        drawComponent(stack, " ^^        (`-=-=-=-=-`)", 32);
        drawComponent(stack, "         (`-=-=-=-=-=-=-`)  ^^", 40);
        drawComponent(stack, "   ^^   (`-=-=-=-=-=-=-=-`)   ^^", 48);
        drawComponent(stack, "       (`-=-=-=-=(@)=-=-=-`)", 56);
        drawComponent(stack, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 64);
        drawComponent(stack, "       (`-=-=-=-=-=-=-=-=-`)", 72);
        drawComponent(stack, "       (`-=-=-=-=-=-=-=-=-`)", 80);
        drawComponent(stack, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 88);
        drawComponent(stack, "        (`-=-=-=-=-=-=-=-`)", 96);
        drawComponent(stack, "         (`-=-=-=-=-=-=-`)  ^^", 104);
        drawComponent(stack, "           (`-=-=-=-=-`)", 112);
        drawComponent(stack, "            `-=-=-=-=-`", 120);
        stack.popPose();
    }

    public void addResponse(Component response) {
        this.commandHandler.onResponse(response);
    }

    private static String formatAsPercent(double value) {
        return NumberFormat.getPercentInstance().format(value);
    }

    private static void drawComponent(PoseStack stack, String translation, int arg, int y) {
        drawComponent(stack, Component.translatable(translation, arg), y);
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawComponent(PoseStack stack, String translation, int arg, int arg2, int y) {
        drawComponent(stack, Component.translatable(translation, arg, arg2), y);
    }

    private static void drawComponent(PoseStack stack, String translation, String arg, int y) {
        drawComponent(stack, Component.translatable(translation, arg), y);
    }

    private static void drawComponent(PoseStack stack, Component component, int y) {
        TERMINAL_FONT_8.draw(stack, component, 6, y, FONT_COLOR_1);
    }

    private static void drawComponent(PoseStack stack, String component, int y) {
        TERMINAL_FONT_8.draw(stack, component, 6, y, FONT_COLOR_1);
    }
}
