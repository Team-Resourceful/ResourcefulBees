package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OptionImageButton extends TooltipAbstractButton {

    private final int u;
    private final int v;
    private boolean selected;
    private final ResourceLocation texture;

    public OptionImageButton(int xPos, int yPos, int u, int v, boolean selected, ResourceLocation texture) {
        super(xPos, yPos, 20, 20, Component.empty());
        this.texture = texture;
        this.selected = selected;
        this.u = u;
        this.v = v;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void toggle() {
        setSelected(!this.selected);
    }

    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void renderButton(@NotNull PoseStack matrixStack, int pMouseX, int pMouseY, float partialTicks) {
        RenderUtils.bindTexture(texture);
        blit(matrixStack, this.x, this.y, isSelected() ? u + 20 : u, isHovered(pMouseX, pMouseY) ? v + 20 : v, this.width, this.height);
    }

    @Override
    public void onPress() {
        this.toggle();
    }
}
