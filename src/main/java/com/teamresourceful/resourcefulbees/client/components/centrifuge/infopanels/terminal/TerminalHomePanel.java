package com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.client.CommandPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.FONT_COLOR_1;
import static com.teamresourceful.resourcefulbees.client.utils.TextUtils.TERMINAL_FONT_8;

public class TerminalHomePanel extends AbstractInfoPanel<CentrifugeTerminalEntity> {

    //TODO neofetch and command input should probably be split into two separate widgets
    //TODO need to clean up x/y pos for all elements
    //TODO charTyped and keyPressed methods don't fire
    //TODO figure why the fuck the owner value keeps going to null
    private String commandInput = "";
    private boolean neofetch = true;
    private final List<FormattedCharSequence> consoleHistory = new ArrayList<>();
    private final CentrifugeState centrifugeState; //this should probably not be final and instead updatable
    private final int tX;

    public TerminalHomePanel(int x, int y, CentrifugeState centrifugeState, CentrifugeTerminalEntity terminal) {
        super(x, y);
        this.centrifugeState = centrifugeState;
        this.selectedEntity = terminal;
        this.tX = x+10;
        init();
    }

    @Override
    protected void init() {
        setFocused(this);
        //changeFocus(true);
/*        if (selectedEntity == null) return;
        if (neofetch) {
            addRenderableWidget(new NeofetchWidget(x, y, selectedEntity.getCentrifugeState()));
        } else {
            addRenderableWidget(new TerminalCommandWidget(x, y));
        }*/
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        // only one terminal allowed per multiblock
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        if (neofetch) {
            drawASCII(stack);
            stack.pushPose();
            stack.translate(tX + 90d, y+30d, 0);
            //TODO make these translatable texts
            String owner = centrifugeState.getOwner()+"@centrifuge";
            //TODO make each of these lines a separate static method with a common font draw method so they are cleaner
            TERMINAL_FONT_8.draw(stack, owner, 6, 0, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, StringUtils.repeat('─', owner.length()), 6, 8, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Max Tier: " + StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 6, 16, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Energy Capacity: " + centrifugeState.getEnergyCapacity() + "rf", 6, 24, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Inputs: " + centrifugeState.getInputs().size(), 6, 32, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Item Outputs: " + centrifugeState.getItemOutputs().size(), 6, 40, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Fluid Outputs: " + centrifugeState.getFluidOutputs().size(), 6, 48, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Voids: " + centrifugeState.getDumps().size(), 6, 56, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Energy Ports: " + centrifugeState.getEnergyPorts(), 6, 64, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Gearboxes: " + centrifugeState.getGearboxes(), 6, 72, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Processors: " + centrifugeState.getProcessors(), 6, 80, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Recipe Power Modifier: " + NumberFormat.getPercentInstance().format(centrifugeState.getRecipePowerModifier()), 6, 88, FONT_COLOR_1);
            TERMINAL_FONT_8.draw(stack, "Recipe Time Modifier: " + NumberFormat.getPercentInstance().format(centrifugeState.getRecipeTimeModifier()), 6, 96, FONT_COLOR_1);
            stack.popPose();
            float pos = 180;
            for (Component component : formatUserInput(commandInput)) {
                TERMINAL_FONT_8.draw(stack, component, tX + 4f, pos, FONT_COLOR_1);
                pos += 10f;
            }
        } else {
            stack.pushPose();
            stack.translate(tX-2f, y+20d, 0);
            float pos = 0;
            int offset = Math.max(consoleHistory.size() - 12, 0);
            for (int i = offset; i < consoleHistory.size(); i++) {
                pos = (i - offset) * 10f;
                TERMINAL_FONT_8.draw(stack, consoleHistory.get(i), 6, pos, FONT_COLOR_1);
            }
            pos += pos > 0 ? 10f : 0f;
            for (Component component : formatUserInput(commandInput)) {
                TERMINAL_FONT_8.draw(stack, component, 6, pos, FONT_COLOR_1);
                pos += 10f;
            }
            stack.popPose();
        }
    }

    @Override
    public boolean charTyped(char typedChar, int modifiers) {
        if (TERMINAL_FONT_8.width(commandInput + typedChar) <= 200) commandInput += typedChar;
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        return switch (key) {
            case GLFW.GLFW_KEY_E -> true;
            case GLFW.GLFW_KEY_BACKSPACE -> onBackspaceKey();
            case GLFW.GLFW_KEY_ENTER -> onEnterKey();
            default -> super.keyPressed(key, scan, modifiers);
        };
    }

    private boolean onBackspaceKey() {
        commandInput = StringUtils.chop(commandInput);
        return true;
    }

    private boolean onEnterKey() {
        commandInput = commandInput.toLowerCase(Locale.ROOT);
        switch (commandInput) {
            case "clear" -> {
                consoleHistory.clear();
                neofetch = false;
            }
            case "neofetch" -> neofetch = true;
            default -> {
                formatUserInput(commandInput).forEach(this::onTerminalResponse);
                if (selectedEntity != null) NetPacketHandler.CHANNEL.sendToServer(new CommandPacket(selectedEntity.getBlockPos(), commandInput));
                neofetch = false;
            }
        }
        commandInput = "";
        return true;
    }

    public void onTerminalResponse(Component component) {
        //max 59 chars
        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(component, 215, TERMINAL_FONT_8);
        consoleHistory.addAll(list);
        System.out.println(component.getString());
    }

    private void drawASCII(@NotNull PoseStack stack) {
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

    private List<Component> formatUserInput(String input) {
        List<Component> components = new ArrayList<>();
        MutableComponent component = Component.literal("╭─").withStyle(ChatFormatting.GRAY);
        component.append(Component.literal(Minecraft.getInstance().getUser().getName()).withStyle(ChatFormatting.BLUE));
        component.append(Component.literal("@").withStyle(ChatFormatting.WHITE));
        component.append(Component.literal("centrifuge").withStyle(ChatFormatting.GREEN));
        components.add(component);
        MutableComponent inputComponent = Component.literal("╰─> ").withStyle(ChatFormatting.GRAY);
        String[] splits = input.split(" ");
        if (splits.length > 1) {
            inputComponent.append(Component.literal(splits[0]).withStyle(ChatFormatting.YELLOW));
            inputComponent.append(Component.literal(" " + String.join(" ", Arrays.copyOfRange(splits, 1, splits.length))).withStyle(ChatFormatting.AQUA));
        } else {
            inputComponent.append(Component.literal(input).withStyle(ChatFormatting.YELLOW));
        }
        components.add(inputComponent);
        return components;
    }
}
