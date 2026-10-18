package com.teamresourceful.resourcefulbees.client.screen.base;

import com.teamresourceful.resourcefulbees.common.components.BatteryData;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.util.TryResources;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CustomWidgetRenderers {

    private static final Identifier BATTERY = ModIdentifier.of("components/battery");
    private static final Identifier TANK = ModIdentifier.of("components/tank");

    private static TryResources scissor(GuiGraphicsExtractor graphics, WidgetRendererContext<?> ctx, float percentage) {
        if (ctx.getWidth() > ctx.getHeight()) {
            int width = (int) Math.clamp(percentage * ctx.getWidth(), 0, ctx.getWidth());
            graphics.enableScissor(ctx.getLeft() + width, ctx.getTop(), ctx.getRight(), ctx.getBottom());
        } else {
            int height = (int) Math.clamp(percentage * ctx.getHeight(), 0, ctx.getHeight());
            graphics.enableScissor(ctx.getLeft(), ctx.getBottom() - height, ctx.getRight(), ctx.getBottom());
        }
        return graphics::disableScissor;
    }

    public static <T extends AbstractWidget> WidgetRenderer<T> hoverable(WidgetRenderer<T> normal, WidgetRenderer<T> hovered) {
        return (graphics, context, ticks) ->
            (context.isHovered() ? hovered : normal).render(graphics, context, ticks);
    }

    public static <T extends AbstractWidget> WidgetRenderer<T> toggleable(State<Boolean> state, WidgetSprites sprites) {
        return (graphics, context, _) -> graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprites.get(state.get(), context.getWidget().isHovered()),
                context.getX(), context.getY(),
                context.getWidth(), context.getHeight()
        );
    }

    public static <T extends AbstractWidget> WidgetRenderer<T> battery(Supplier<BatteryData> data) {
        return (graphics, ctx, _) -> {
            var battery = data.get();
            try (var _ = CustomWidgetRenderers.scissor(graphics, ctx, battery.percentage())) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BATTERY, ctx.getX(), ctx.getY(), ctx.getWidth(), ctx.getHeight());
            }

            if (ctx.isHovered()) {
                var format = NumberFormat.getNumberInstance();
                graphics.setTooltipForNextFrame(
                        Component.literal("%s / %s RF".formatted(format.format(battery.energy()), format.format(battery.capacity()))),
                        ctx.getMouseX(),
                        ctx.getMouseY()
                );
            }
        };
    }

    public static <T extends AbstractWidget> WidgetRenderer<T> tank(Supplier<TankData> data) {
        return WidgetRenderers.layered(
                WidgetRenderers.sprite(TANK),
                (graphics, ctx, _) -> {
                    var tank = data.get();
                    var stack = tank.fluid();
                    if (!stack.isEmpty()) {
                        var state = stack.getFluid().defaultFluidState();
                        var model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state);
                        var tint = model.fluidTintSource();
                        var texture = model.stillMaterial().sprite();

                        try (var _ = CustomWidgetRenderers.scissor(graphics, ctx, tank.percentage())) {
                            for (int x = 0; x <= ctx.getWidth() / texture.contents().width(); x++) {
                                for (int y = 0; y <= ctx.getHeight() / texture.contents().height(); y++) {
                                    graphics.blitSprite(
                                            RenderPipelines.GUI_TEXTURED,
                                            texture,
                                            ctx.getX() + x * texture.contents().width(),
                                            ctx.getY() + y * texture.contents().height(),
                                            texture.contents().width(),
                                            texture.contents().height(),
                                            tint == null ? -1 : tint.color(state)
                                    );
                                }
                            }
                        }
                    }

                    if (ctx.isHovered()) {
                        var format = NumberFormat.getNumberInstance();
                        graphics.setTooltipForNextFrame(
                                Minecraft.getInstance().font,
                                stack.isEmpty() ? List.of(Component.literal("None")) : List.of(
                                        stack.getHoverName(),
                                        Component.literal("%s Buckets".formatted(format.format(stack.amount() / 1000.0)))
                                ),
                                Optional.empty(),
                                ctx.getMouseX(),
                                ctx.getMouseY()
                        );
                    }
                },
                WidgetRenderers.sprite(TANK)
        );
    }
}
