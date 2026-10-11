package com.teamresourceful.resourcefulbees.client.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class BeeconToggleWidget extends AbstractWidget {
    // RID: Added Width and Height variables instead of hard-coded as I needed more control over them.
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int w;
    private final int h;
    private boolean selected;

    public BeeconToggleWidget(int x, int y, int u, int v, int w, int h, boolean selected, Identifier texture) {
        super(x, y, w, h, Component.empty());
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.selected = selected;
        this.texture = texture;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // RID: Added hover effect.
        int v1 = this.selected ? 0 : v + h + 19;
        if (this.isHovered()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 256, 256, u, v1 + 19, getX(), getY(), w, h);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 256, 256, u, v1, getX(), getY(), w, h);
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        setSelected(!this.selected);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        // document why this method is empty
    }
}