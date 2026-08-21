package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.Supplier;

public class TankWidget extends AbstractWidget {


    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
    public static final Tooltip EMPTY_TOOLTIP = Tooltip.create(Component.literal("Empty"));
    private final Supplier<List<TankData>> tankData;
    private double scrollAccumulator;

    private int selectedTank = 0;

    private TankWidget(
            int x,
            int y,
            int width,
            int height,
            Supplier<List<TankData>> tankData
    ) {
        super(x, y, width, height, Component.empty());
        this.tankData = tankData;
    }

    public static TankWidget single(int x, int y, int width, int height, Supplier<TankData> tank) {
        return new TankWidget(x, y, width, height, () -> List.of(tank.get()));
    }

    public static TankWidget selectable(int x, int y, int width, int height, Supplier<List<TankData>> tanks) {
        return new TankWidget(x, y, width, height, tanks);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        TankData data = getSelectedTank();
        
        ClientRenderUtils.drawTank(graphics, data, getX(), getY(), getWidth(), getHeight());

        if (isHovered()) {
            setTooltip(createTooltip(data));
        } else {
            setTooltip(null);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isHovered() || !Minecraft.getInstance().hasShiftDown()) {
            return false;
        }

        List<TankData> tanks = tankData.get();

        if (tanks.size() <= 1 || (scrollX == 0 && scrollY == 0)) {
            return false;
        }

        double scroll = Math.abs(scrollY) >= Math.abs(scrollX) ? scrollY : scrollX;
        scrollAccumulator += scroll;

        if (Math.abs(scrollAccumulator) < 1.0) {
            return true;
        }

        int direction = scrollAccumulator > 0 ? -1 : 1;
        selectedTank = Math.floorMod(selectedTank + direction, tanks.size());
        scrollAccumulator = 0;

        return true;
    }

    private TankData getSelectedTank() {
        List<TankData> tanks = tankData.get();

        if (tanks.isEmpty()) {
            selectedTank = 0;
            return TankData.EMPTY;
        }

        // Protect against the synchronized list shrinking while
        // the screen is open.
        selectedTank = Math.floorMod(selectedTank, tanks.size());

        return tanks.get(selectedTank);
    }

    private Tooltip createTooltip(TankData data) {
        FluidStack fluidStack = data.fluid();

        //if (fluidStack.isEmpty()) {
        //    return EMPTY_TOOLTIP;
        //}

        long amount = fluidStack.amount();

        Component amountText;

        if (Minecraft.getInstance().hasShiftDown() || amount < 500) {
            amountText = Component.literal(
                    NUMBER_FORMAT.format(amount) + " mB"
            );
        } else {
            amountText = Component.literal(
                    NUMBER_FORMAT.format(amount / 1000.0) + " Buckets"
            );
        }

        var tooltip = Component.empty()
                .append(fluidStack.getHoverName())
                .append("\n")
                .append(amountText);

        List<TankData> tanks = tankData.get();

        if (tanks.size() > 1) {
            tooltip.append("\n")
                    .append(Component.literal(
                            "Tank "
                                    + (selectedTank + 1)
                                    + "/"
                                    + tanks.size()
                    ));

            if (Minecraft.getInstance().hasShiftDown()) {
                tooltip.append("\n")
                        .append(Component.literal(
                                "Scroll to change tank"
                        ));
            }
        }

        return Tooltip.create(tooltip);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        //
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundManager) {
        // shut it
    }
}
