package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.terminal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.AbstractTerminalModule;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.centrifuge.CommandMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.common.utils.RenderUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.common.utils.RenderUtils.TERMINAL_FONT_8;

public class TerminalHomeModule extends AbstractTerminalModule<CentrifugeTerminalScreen> {

    private static final int TEXT_X = 110;

    private String commandInput = "";
    private boolean neofetch = true;
    private final List<FormattedCharSequence> consoleHistory = new ArrayList<>();

    public TerminalHomeModule(CentrifugeTerminalScreen screen) {
        super(screen);
    }

    @Override
    public void renderText(PoseStack matrix, int mouseX, int mouseY) {
        if (neofetch) {
            drawASCII(matrix);
            matrix.pushPose();
            matrix.translate(TEXT_X + 90d, 66, 0);
            CentrifugeState centrifugeState = screen.getCentrifugeState();
            //TODO make these translatable texts
            String owner = centrifugeState.getOwner()+"@centrifuge";
            TERMINAL_FONT_8.draw(matrix, owner, 6, 0, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, StringUtils.repeat('-', owner.length()), 6, 8, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Max Tier: " + StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 6, 16, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Energy Capacity: " + centrifugeState.getEnergyCapacity() + "rf", 6, 24, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Inputs: " + centrifugeState.getInputs().size(), 6, 32, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Item Outputs: " + centrifugeState.getItemOutputs().size(), 6, 40, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Fluid Outputs: " + centrifugeState.getFluidOutputs().size(), 6, 48, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Energy Ports: " + centrifugeState.getEnergyPorts(), 6, 56, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Voids: " + centrifugeState.getDumps().size(), 6, 64, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Gearboxes: " + centrifugeState.getGearboxes(), 6, 72, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Processors: " + centrifugeState.getProcessors(), 6, 80, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Recipe Power Modifier: " + centrifugeState.getRecipePowerModifier() * 100 + "%", 6, 88, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(matrix, "Recipe Time Modifier: " + ModConstants.DECIMAL_FORMAT.format(centrifugeState.getRecipeTimeModifier() * 96) + "%", 6, 104, FONT_COLOR_1);
            matrix.popPose();
            TERMINAL_FONT_8.draw(matrix, formatUserInput(commandInput), TEXT_X + 4f, 186, FONT_COLOR_1);
        } else {
            matrix.pushPose();
            matrix.translate(TEXT_X - 2f, 66, 0);
            float pos = 0;
            int offset = Math.max(consoleHistory.size() - 12, 0);
            for (int i = offset; i < consoleHistory.size(); i++) {
                pos = (i - offset) * 10f;
                TERMINAL_FONT_8.draw(matrix, consoleHistory.get(i), 6, pos, FONT_COLOR_1);
            }
            pos += pos > 0 ? 10f : 0f;
            TERMINAL_FONT_8.draw(matrix, formatUserInput(commandInput), 6, pos, FONT_COLOR_1);
            matrix.popPose();
        }
    }

    @Override
    public boolean onCharTyped(char typedChar, int modifiers) {
        if (commandInput.length() < 34) commandInput += typedChar;
        return true;
    }

    @Override
    public boolean onKeyPressed(int key, int scan, int modifiers) {
        switch (key) {
            case 69: return true;
            case 259:
                commandInput = StringUtils.chop(commandInput);
                return true;
            case 257:
                commandInput = commandInput.toLowerCase(Locale.ROOT);
                switch (commandInput) {
                    case "clear" -> {
                        consoleHistory.clear();
                        neofetch = false;
                    }
                    case "neofetch" -> neofetch = true;
                    default -> {
                        CentrifugeTerminalEntity terminal = screen.getMenu().getEntity();
                        onTerminalResponse(formatUserInput(commandInput));
                        NetPacketHandler.sendToServer(new CommandMessage(terminal.getBlockPos(), commandInput));
                        neofetch = false;
                    }
                }
                commandInput = "";
                return true;
            default: return super.onKeyPressed(key, scan, modifiers);
        }
    }

    @Override
    public void onTerminalResponse(Component component) {
        //max 59 chars
        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(component, 215, TERMINAL_FONT_8);
        consoleHistory.addAll(list);
        System.out.println(component.getString());
    }

    private void drawASCII(@NotNull PoseStack matrix) {
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
         */
        matrix.pushPose();
        matrix.translate(TEXT_X - 10d, 66, 0);
        matrix.scale(0.8f, 0.8f, 0.8f);
        TERMINAL_FONT_8.draw(matrix, "     ^^      .-=-=-=-.  ^^", 6, 24, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, " ^^        (`-=-=-=-=-`)", 6, 32, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "         (`-=-=-=-=-=-=-`)  ^^", 6, 40, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "   ^^   (`-=-=-=-=-=-=-=-`)   ^^", 6, 48, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=(@)=-=-=-`)", 6, 56, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 6, 64, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)", 6, 72, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)", 6, 80, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 6, 88, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "        (`-=-=-=-=-=-=-=-`)", 6, 96, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "         (`-=-=-=-=-=-=-`)  ^^", 6, 104, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "           (`-=-=-=-=-`)", 6, 112, FONT_COLOR_1);
        TERMINAL_FONT_8.draw(matrix, "            `-=-=-=-=-`", 6, 120, FONT_COLOR_1);
        matrix.popPose();
    }

    private Component formatUserInput(String input) {
        MutableComponent component = new TextComponent(Minecraft.getInstance().getUser().getName()).withStyle(ChatFormatting.BLUE);
        component.append(new TextComponent("@").withStyle(ChatFormatting.WHITE));
        component.append(new TextComponent("centrifuge").withStyle(ChatFormatting.GREEN));
        component.append(new TextComponent("> ").withStyle(ChatFormatting.GRAY));
        String[] splits = input.split(" ");
        if (splits.length > 1) {
            component.append(new TextComponent(splits[0]).withStyle(ChatFormatting.YELLOW));
            component.append(new TextComponent(" " + String.join(" ", Arrays.copyOfRange(splits, 1, splits.length))).withStyle(ChatFormatting.AQUA));
        } else {
            component.append(new TextComponent(input).withStyle(ChatFormatting.YELLOW));
        }
        return component;
    }
}
