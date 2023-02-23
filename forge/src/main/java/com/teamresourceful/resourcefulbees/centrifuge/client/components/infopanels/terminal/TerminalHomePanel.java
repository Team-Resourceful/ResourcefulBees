package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.TerminalCommandHandler;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.TERMINAL_FONT_8;

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
            TERMINAL_FONT_8.draw(stack, owner, 6, 0, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, StringUtils.repeat('─', owner.length()), 6, 8, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Max Tier: " + StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 6, 16, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Energy: " + energyStorage.getStored() + "/" + energyStorage.getCapacity() + "rf", 6, 24, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Inputs: " + centrifugeState.getInputs().size(), 6, 32, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Item Outputs: " + centrifugeState.getItemOutputs().size(), 6, 40, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Fluid Outputs: " + centrifugeState.getFluidOutputs().size(), 6, 48, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Voids: " + centrifugeState.getFilters().size(), 6, 56, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Energy Ports: " + centrifugeState.getEnergyPorts(), 6, 64, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Gearboxes: " + centrifugeState.getGearboxes(), 6, 72, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Processors: " + centrifugeState.getProcessors(), 6, 80, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Recipe Power Modifier: " + NumberFormat.getPercentInstance().format(centrifugeState.getRecipePowerModifier()), 6, 88, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Recipe Time Modifier: " + NumberFormat.getPercentInstance().format(centrifugeState.getRecipeTimeModifier()), 6, 96, FONT_COLOR_1);
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
        TERMINAL_FONT_8.draw(stack, "     ^^      .-=-=-=-.  ^^", 6, 24, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, " ^^        (`-=-=-=-=-`)", 6, 32, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "         (`-=-=-=-=-=-=-`)  ^^", 6, 40, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "   ^^   (`-=-=-=-=-=-=-=-`)   ^^", 6, 48, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "       (`-=-=-=-=(@)=-=-=-`)", 6, 56, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 6, 64, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "       (`-=-=-=-=-=-=-=-=-`)", 6, 72, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "       (`-=-=-=-=-=-=-=-=-`)", 6, 80, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 6, 88, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "        (`-=-=-=-=-=-=-=-`)", 6, 96, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "         (`-=-=-=-=-=-=-`)  ^^", 6, 104, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "           (`-=-=-=-=-`)", 6, 112, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "            `-=-=-=-=-`", 6, 120, FONT_COLOR_1);
        stack.popPose();
    }

    public void addResponse(Component response) {
        this.commandHandler.onResponse(response);
    }
}
