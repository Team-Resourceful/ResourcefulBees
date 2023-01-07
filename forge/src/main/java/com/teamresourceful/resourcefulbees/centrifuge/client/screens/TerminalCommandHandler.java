package com.teamresourceful.resourcefulbees.centrifuge.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.CommandPacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.TERMINAL_FONT_8;

public final class TerminalCommandHandler {

    private final List<FormattedCharSequence> history = new ArrayList<>();
    private final CentrifugeTerminalEntity terminal;

    private String input = "";
    private boolean neofetch = true;

    public TerminalCommandHandler(CentrifugeTerminalEntity terminal) {
        this.terminal = terminal;
    }

    public void charTyped(char typedChar) {
        if (TERMINAL_FONT_8.width(this.input + typedChar) <= 200) {
            this.input += typedChar;
        }
    }

    public boolean keyPressed(int key) {
        return switch (key) {
            case GLFW.GLFW_KEY_E -> true;
            case GLFW.GLFW_KEY_BACKSPACE -> {
                onBackspaceKey();
                yield true;
            }
            case GLFW.GLFW_KEY_ENTER -> {
                onEnterKey();
                yield true;
            }
            default -> false;
        };
    }

    private void onBackspaceKey() {
        this.input = StringUtils.chop(this.input);
    }

    private void onEnterKey() {
        this.input = this.input.toLowerCase(Locale.ROOT);
        this.neofetch = switch (this.input) {
            case "clear" -> {
                this.history.clear();
                yield false;
            }
            case "neofetch" -> true;
            default -> {
                onResponse(getUserInput());
                if (this.terminal != null) {
                    NetworkHandler.CHANNEL.sendToServer(new CommandPacket(this.terminal.getBlockPos(), this.input));
                }
                yield false;
            }
        };
        this.input = "";
    }

    public Component getUserInput() {
        MutableComponent inputComponent = Component.literal("> ").withStyle(ChatFormatting.GRAY);
        String[] splits = this.input.split(" ");
        if (splits.length > 1) {
            inputComponent.append(Component.literal(splits[0]).withStyle(ChatFormatting.YELLOW));
            inputComponent.append(Component.literal(" " + String.join(" ", Arrays.copyOfRange(splits, 1, splits.length))).withStyle(ChatFormatting.AQUA));
        } else {
            inputComponent.append(Component.literal(this.input).withStyle(ChatFormatting.YELLOW));
        }
        return inputComponent;
    }

    public boolean isNeofetch() {
        return this.neofetch;
    }

    public List<FormattedCharSequence> getHistory() {
        return this.history;
    }

    public List<FormattedCharSequence> getLastHistory(int amount) {
        return getHistory().subList(Math.max(0, this.history.size() - amount), this.history.size());
    }

    public void render(PoseStack stack, int x, int y) {
        stack.pushPose();
        stack.translate(x-2f, y+20d, 0);
        float pos = 0;
        List<FormattedCharSequence> history = this.getLastHistory(12);
        for (int i = 0; i < history.size(); i++) {
            pos = i * TERMINAL_FONT_8.lineHeight;
            TERMINAL_FONT_8.draw(stack, history.get(i), 6, pos, FONT_COLOR_1);
        }
        drawInput(stack, 6, pos + TERMINAL_FONT_8.lineHeight);
        stack.popPose();
    }

    public void drawInput(PoseStack stack, float x, float y) {
        TERMINAL_FONT_8.draw(stack, this.getUserInput(), x, y, FONT_COLOR_1);
        long time = System.currentTimeMillis() % 1000;
        if (time / 500 == 0 && TERMINAL_FONT_8.width(this.input + "i") <= 200) {
            Component underline = Component.literal("_").withStyle(Style.EMPTY.withColor(FONT_COLOR_1));
            int width = TERMINAL_FONT_8.width(this.getUserInput());
            TERMINAL_FONT_8.draw(stack, underline, x + width, y, FONT_COLOR_1);
        }
    }

    public void onResponse(Component component) {
        history.addAll(ComponentRenderUtils.wrapComponents(component, 215, TERMINAL_FONT_8));
    }
}
