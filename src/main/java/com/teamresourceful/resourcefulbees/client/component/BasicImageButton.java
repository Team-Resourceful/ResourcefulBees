package com.teamresourceful.resourcefulbees.client.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class BasicImageButton extends AbstractWidget {

    private final Identifier texture;
    private final int u;
    private final int v;
    private boolean selected;

    public BasicImageButton(int x, int y, int u, int v, boolean selected, Identifier texture) {
        super(x, y, 20, 20, Component.empty());
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int textureU = this.selected ? this.u + 20 : this.u;
        int textureV = this.isHovered() ? this.v + 20 : this.v;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.texture,
                this.getX(),
                this.getY(),
                textureU,
                textureV,
                20,
                20,
                20,
                20,
                256,
                256);
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        setSelected(!this.selected);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        // document why this method is empty
    }
}
