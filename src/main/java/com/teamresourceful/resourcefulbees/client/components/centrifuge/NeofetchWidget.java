/*
package com.teamresourceful.resourcefulbees.client.components.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.NumberFormat;

import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.TERMINAL_FONT_8;

public class NeofetchWidget extends ParentWidget {
    
    private final CentrifugeState centrifugeState;
    
    public NeofetchWidget(int x, int y, CentrifugeState centrifugeState) {
        super(x, y);
        this.centrifugeState = centrifugeState;
    }

    @Override
    protected void init() {
        
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        drawASCII(stack);
        String owner = centrifugeState.getOwner()+"@centrifuge";
        TERMINAL_FONT_8.draw(stack, owner, 6, 0, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, StringUtils.repeat('─', owner.length()), 6, 8, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Max Tier: " + StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 6, 16, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Energy Capacity: " + centrifugeState.getEnergyCapacity() + "rf", 6, 24, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Inputs: " + centrifugeState.getInputs().size(), 6, 32, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Item Outputs: " + centrifugeState.getItemOutputs().size(), 6, 40, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Fluid Outputs: " + centrifugeState.getFluidOutputs().size(), 6, 48, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Energy Ports: " + centrifugeState.getEnergyPorts(), 6, 56, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Voids: " + centrifugeState.getDumps().size(), 6, 64, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Gearboxes: " + centrifugeState.getGearboxes(), 6, 72, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Processors: " + centrifugeState.getProcessors(), 6, 80, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Recipe Power Modifier: " + NumberFormat.getPercentInstance().format(centrifugeState.getRecipePowerModifier()), 6, 88, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(stack, "Recipe Time Modifier: " + NumberFormat.getPercentInstance().format(centrifugeState.getRecipeTimeModifier()), 6, 104, FONT_COLOR_1);
        stack.popPose();
        float pos = 180;
        for (Component component : formatUserInput(commandInput)) {
            TERMINAL_FONT_8.draw(stack, component, TEXT_X + 4f, pos, FONT_COLOR_1);
            pos += 10f;
        }
    }

    private void drawASCII(@NotNull PoseStack stack) {
        */
/*
     ^^      .-=-=-=-.  ^^
 ^^        (`-=-=-=-=-`)         ^^
        (`-=-=-=-=-=-=-`)  ^^         ^^
   ^^   (`-=-=-=-=-=-=-=-`)   ^^                            ^^
        ( `-=-=-=-(@)-=-=-` )      ^^
        (`-=-=-=-=-=-=-=-=-`)  ^^
        (`-=-=-=-=-=-=-=-=-`)              ^^
        (`-=-=-=-=-=-=-=-=-`)                      ^^
        (`-=-=-=-=-=-=-=-=-`)  ^^
        (`-=-=-=-=-=-=-=-`)          ^^
        (`-=-=-=-=-=-=-`)  ^^                 ^^
        jgs   (`-=-=-=-=-`)
            `-=-=-=-=-`
         *//*

        stack.pushPose();
        stack.translate(TEXT_X - 10d, 66, 0);
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
}
*/
