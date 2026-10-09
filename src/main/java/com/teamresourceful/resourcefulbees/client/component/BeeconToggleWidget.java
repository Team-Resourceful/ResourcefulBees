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

    private final Identifier texture;
    private final int u;
    private final int v;
    private boolean selected;

    public BeeconToggleWidget(int x, int y, int u, int v, boolean selected, Identifier texture) {
        super(x, y, 14, 14, Component.empty());
        this.u = u;
        this.v = v;
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
        //int textureU = this.selected ? this.u + 20 : this.u;
        //int textureV = this.isHovered() ? this.v + 20 : this.v;

        //graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 256, 256, textureU, textureV, this.getX(), this.getY(), 20, 20);

        int v1 = this.selected ? 0 : v + 15;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 256, 256, u, v1, getX(), getY(), 14, 14);
        //graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, 256, 256, u, v1, getX(), getY() + 15, 14, 14);
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
