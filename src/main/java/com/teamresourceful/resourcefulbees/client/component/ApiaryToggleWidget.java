package com.teamresourceful.resourcefulbees.client.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class ApiaryToggleWidget extends AbstractWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int w;
    private final int h;
    private boolean locked;

    public ApiaryToggleWidget(int x, int y, int u, int v, int w, int h, boolean locked, Identifier texture) {
        super(x, y, w, h, Component.empty());
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.locked = locked;
        this.texture = texture;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int u1 = !this.locked ? 0 : u + w;
        if (this.isHovered()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 60, 60, u1, v + 28, getX(), getY(), w, h);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 60, 60, u1, v, getX(), getY(), w, h);
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        setLocked(!this.locked);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        // document why this method is empty
    }
}