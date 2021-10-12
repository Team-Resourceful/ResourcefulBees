package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ToggleImageButton extends Button {

    private final int textureWidth;
    private final int textureHeight;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final ResourceLocation resourceLocation;
    public boolean enabled = false;
    public IPressable pressable;

    public ToggleImageButton(int xPos, int yPos, int xOffset, boolean enabled, ButtonTemplate template, IPressable pressable, ITextComponent message) {
        this(xPos, yPos, template.width, template.height, template.xTexStart + xOffset, enabled, template.yTexStart, template.yTexDiff, template.resourceLocation, template.textureWidth, template.textureHeight, pressable, message);
    }

    public ToggleImageButton(int xPos, int yPos, int width, int height, int xTexStart, boolean enabled, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, IPressable pressable, ITextComponent message) {
        super(xPos, yPos, width, height, message, p -> {}, (t1, t2, t3, t4) -> {});
        this.pressable = pressable;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = resourceLocation;
        this.enabled = enabled;
    }

    @Override
    public void onPress() {
        this.enabled = !enabled;
        this.pressable.onPress(this);
    }

    @Override
    public void renderButton(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);
        int i = this.yTexStart;
        if (this.isHovered() && active) {
            i += this.yDiffTex;
        } else if (this.enabled) {
            i += this.yDiffTex * 2;
        }

        RenderSystem.enableDepthTest();
        blit(matrixStack, this.x, this.y, (float)this.xTexStart, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderToolTip(@NotNull MatrixStack matrixStack, int mouseX, int mouseY) {
        this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
        void onPress(ToggleImageButton button);
    }
}
