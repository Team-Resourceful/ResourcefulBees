package com.teamresourceful.resourcefulbees.client.screen.base;

import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;

public class CustomWidgetRenderers {

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
}
