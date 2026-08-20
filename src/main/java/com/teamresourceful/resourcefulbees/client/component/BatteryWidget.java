package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.common.components.BatteryData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.text.NumberFormat;
import java.util.function.Supplier;

public class BatteryWidget extends AbstractWidget {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private final Supplier<BatteryData> battery;
    private final Identifier texture;
    private final int textureU;
    private final int textureV;

    public BatteryWidget(
            int x,
            int y,
            int width,
            int height,
            int textureU,
            int textureV,
            Identifier texture,
            Supplier<BatteryData> battery
    ) {
        super(x, y, width, height, Component.empty());

        this.battery = battery;
        this.texture = texture;
        this.textureU = textureU;
        this.textureV = textureV;
    }

    @Override
    protected void extractWidgetRenderState(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        BatteryData batteryData = battery.get();
        int amount = batteryData.energy();
        int capacity = batteryData.capacity();

        if (amount > 0 && capacity > 0) {
            int energyHeight = (int) Math.clamp(
                    (long) amount * getHeight() / capacity,
                    0L,
                    getHeight()
            );

            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    getX(),
                    getY() + getHeight() - energyHeight,
                    textureU,
                    textureV + getHeight() - energyHeight,
                    getWidth(),
                    energyHeight,
                    256,
                    256
            );
        }

        if (isHovered()) {
            setTooltip(createTooltip(amount, capacity));
        } else {
            setTooltip(null);
        }
    }

    private Tooltip createTooltip(int amount, int capacity) {
        return Tooltip.create(
                Component.literal(
                        NUMBER_FORMAT.format(amount)
                                + " / "
                                + NUMBER_FORMAT.format(capacity)
                                + " RF"
                )
        );
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // Rendering-only widget with no interactive action.
    }
}