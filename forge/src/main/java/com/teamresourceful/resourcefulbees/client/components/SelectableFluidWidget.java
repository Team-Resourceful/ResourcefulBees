package com.teamresourceful.resourcefulbees.client.components;

import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ModTranslations;
import com.teamresourceful.resourcefulbees.common.network.packets.client.SelectFluidPacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import com.teamresourceful.resourcefullib.common.collections.SelectableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectableFluidWidget extends AbstractWidget {

    private final SelectableMultiFluidTank tank;
    private final int id;
    private final BlockPos pos;
    private FluidStack lastStack;

    public SelectableFluidWidget(SelectableMultiFluidTank tank, int id, BlockPos pos, int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
        this.tank = tank;
        this.id = id;
        this.pos = pos;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            int height = Math.round((fluid.getAmount() / (float)tank.getCapacity()) * this.height);
            ClientUtils.drawFluid(graphics, height, this.width, fluid, this.getX(), this.getY() + this.height - height);
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
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty()) {
            tooltip.add(ModTranslations.TANK_EMPTY);
        } else {
            tooltip.add(ModTranslations.FLUID_SWITCH);
            tooltip.add(CommonComponents.EMPTY);
            for (FluidStack tankFluid : tank.getFluids()) {
                MutableComponent line;
                if (this.lastStack != null ? tankFluid.isFluidEqual(this.lastStack) : tankFluid.isFluidEqual(fluid)) {
                    line = Component.literal(" ● ").withStyle(ChatFormatting.GREEN);
                } else {
                    line = Component.literal(" ○ ").withStyle(ChatFormatting.DARK_GRAY);
                }
                line = line.append(Component.empty().withStyle(ChatFormatting.RESET).append(tankFluid.getDisplayName()));
                line = line.append(Component.literal(" " + tankFluid.getAmount() + "mb").withStyle(ChatFormatting.RESET));
                tooltip.add(line);
            }
        }
        ScreenUtils.setTooltip(tooltip);
    }

    @Override
    public boolean mouseScrolled(double scrollX, double scrollY, double delta) {
        //TODO consider adding up/down arrow key support for people who may not have a scroll wheel
        if (this.isHoveredOrFocused() && Screen.hasControlDown()) {
            this.lastStack = nextStack(this.lastStack != null ? this.lastStack : this.tank.getFluid(), delta < 0 ? 1 : -1);
        }
        return false;
    }

    private FluidStack nextStack(FluidStack stack, int direction) {
        SelectableList<FluidStack> fluids = this.tank.getFluids();
        int size = fluids.size();
        for (int i = 0; i < size; i++) {
            if (stack.isFluidEqual(fluids.get(i))) {
                int index = (i + direction) % size;
                if (index < 0) index += size;
                FluidStack newStack = fluids.get(index);
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
        if (lastStack != null) {
            if (!this.tank.getFluid().isFluidEqual(lastStack)) {
                NetworkHandler.CHANNEL.sendToServer(new SelectFluidPacket(pos, id, lastStack));
            }
            lastStack = null;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
