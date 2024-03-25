package com.teamresourceful.resourcefulbees.client.component;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ModTranslations;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.SelectFluidPacket;
import com.teamresourceful.resourcefulbees.common.util.containers.SelectableFluidContainer;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import com.teamresourceful.resourcefullib.common.collections.SelectableList;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectableFluidWidget extends AbstractWidget {

    private final SelectableFluidContainer container;
    private final BlockPos pos;
    private FluidHolder lastHolder;

    public SelectableFluidWidget(SelectableFluidContainer container, BlockPos pos, int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
        this.container = container;
        this.pos = pos;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        FluidHolder holder = container.getFluid();
        if (!holder.isEmpty()) {
            int height = Math.round((holder.getFluidAmount() / (float)container.getTankCapacity(0)) * this.height);
            ClientRenderUtils.drawFluid(graphics, height, this.width, holder, this.getX(), this.getY() + this.height - height);
        }
        if (this.isHoveredOrFocused()) {
            this.renderToolTip();
        }
        if (!Screen.hasControlDown()) {
            sendToServer();
        }
    }

    public void renderToolTip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(this.getMessage());
        tooltip.add(CommonComponents.EMPTY);
        FluidHolder holder = container.getFluid();
        if (holder.isEmpty()) {
            tooltip.add(ModTranslations.TANK_EMPTY);
        } else {
            tooltip.add(ModTranslations.FLUID_SWITCH);
            tooltip.add(CommonComponents.EMPTY);
            for (FluidHolder tankFluid : container.getFluids()) {
                MutableComponent line;
                if (this.lastHolder != null ? tankFluid.matches(this.lastHolder) : tankFluid.matches(holder)) {
                    line = Component.literal(" ● ").withStyle(ChatFormatting.GREEN);
                } else {
                    line = Component.literal(" ○ ").withStyle(ChatFormatting.DARK_GRAY);
                }
                line = line.append(Component.empty().withStyle(ChatFormatting.RESET).append(ClientFluidHooks.getDisplayName(tankFluid)));
                line = line.append(Component.literal(" " + FluidConstants.toMillibuckets(tankFluid.getFluidAmount()) + "mb").withStyle(ChatFormatting.RESET));
                tooltip.add(line);
            }
        }
        ScreenUtils.setTooltip(tooltip);
    }

    @Override
    public boolean mouseScrolled(double scrollX, double scrollY, double delta) {
        if (this.isHoveredOrFocused() && Screen.hasControlDown()) {
            this.lastHolder = nextStack(this.lastHolder != null ? this.lastHolder : this.container.getFluid(), delta < 0 ? 1 : -1);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isHoveredOrFocused() && Screen.hasControlDown()) {
            if (keyCode == InputConstants.KEY_UP) {
                this.lastHolder = nextStack(this.lastHolder != null ? this.lastHolder : this.container.getFluid(), -1);
                return true;
            } else if (keyCode == InputConstants.KEY_DOWN) {
                this.lastHolder = nextStack(this.lastHolder != null ? this.lastHolder : this.container.getFluid(), 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private FluidHolder nextStack(FluidHolder stack, int direction) {
        SelectableList<FluidHolder> fluids = this.container.getFluids();
        int size = fluids.size();
        for (int i = 0; i < size; i++) {
            if (stack.matches(fluids.get(i))) {
                int index = (i + direction) % size;
                if (index < 0) index += size;
                FluidHolder newStack = fluids.get(index);
                return newStack.isEmpty() ? null : newStack;
            }
        }
        return null;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            sendToServer();
        }
    }

    private void sendToServer() {
        if (lastHolder != null) {
            if (!this.container.getFluid().matches(lastHolder)) {
                NetworkHandler.CHANNEL.sendToServer(new SelectFluidPacket(pos, lastHolder));
            }
            lastHolder = null;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
