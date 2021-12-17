package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class OptionImageButton extends AbstractButton {

    private final int u;
    private final int v;
    private boolean selected;
    private final ResourceLocation texture;

    public OptionImageButton(int xPos, int yPos, int u, int v, boolean selected, ResourceLocation texture) {
        super(xPos, yPos, 20, 20, StringTextComponent.EMPTY);
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
    public void renderButton(@NotNull MatrixStack matrixStack, int pMouseX, int pMouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(texture);
        //noinspection deprecation
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrixStack, this.x, this.y, isSelected() ? u + 20 : u, isHovered(pMouseX, pMouseY) ? v + 20 : v, this.width, this.height);
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return visible && MathUtils.inRangeInclusive((int) mouseX, x, x + width) && MathUtils.inRangeInclusive((int) mouseY, y, y + height);
    }

    @Override
    public void onPress() {
        this.toggle();
    }
}