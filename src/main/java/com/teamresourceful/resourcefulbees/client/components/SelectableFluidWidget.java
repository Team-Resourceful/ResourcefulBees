package com.teamresourceful.resourcefulbees.client.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SelectFluidMessage;
import com.teamresourceful.resourcefullib.common.utils.SelectableList;
import net.minecraft.ChatFormatting;
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
import java.util.Optional;

public class SelectableFluidWidget extends AbstractWidget {

    private final SelectableMultiFluidTank tank;
    private final Screen screen;
    private final int id;
    private final BlockPos pos;
    private FluidStack lastStack;

    public SelectableFluidWidget(Screen screen, SelectableMultiFluidTank tank, int id, BlockPos pos, int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
        this.screen = screen;
        this.tank = tank;
        this.id = id;
        this.pos = pos;
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            int height = Math.round(((float) fluid.getAmount() / (float)tank.getCapacity()) * (float)this.height);
            RenderUtils.drawFluid(stack, height, this.width, fluid, this.x, this.y + this.height - height, getBlitOffset());
        }
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(stack, mouseX, mouseY);
        }
        if (!Screen.hasControlDown()) {
            sendToServer();
        }
    }

    @Override
    public void renderToolTip(@NotNull PoseStack stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(this.getMessage());
        tooltip.add(CommonComponents.EMPTY);
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty()) {
            tooltip.add(Component.literal("Tank Empty")); //TODO Translate
        } else {
            tooltip.add(Component.literal("Hold [CTRL] to switch between fluids!")); //TODO Translate
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
        screen.renderTooltip(stack, tooltip, Optional.empty(), mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double scrollX, double scrollY, double delta) {
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
    protected void onFocusedChanged(boolean focused) {
        if (!focused) {
            sendToServer();
        }
    }

    private void sendToServer() {
        if (lastStack != null) {
            if (!this.tank.getFluid().isFluidEqual(lastStack)) {
                NetPacketHandler.sendToServer(new SelectFluidMessage(pos, id, lastStack));
            }
            lastStack = null;
        }
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput output) {

    }
}
