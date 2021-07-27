package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class ArrowButton extends Button {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private static final ResourceLocation arrowButtonTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/arrow_button.png");
    private final int xTexStart;

    public ArrowButton(int xIn, int yIn, Direction direction, IPressable onPressIn) {
        super(xIn, yIn, 12, 12, StringTextComponent.EMPTY, onPressIn);
        switch (direction){
            case UP:
                xTexStart = 0;
                break;
            case DOWN:
                xTexStart = 12;
                break;
            case LEFT:
                xTexStart = 24;
                break;
            default:
                xTexStart = 36;
        }
    }

    @Override
    public void renderButton(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(arrowButtonTexture);
        RenderSystem.disableDepthTest();
        if (this.active) {
            blit(matrix, this.x, this.y, (float)this.xTexStart, this.isHovered() ? 12f : 0f, this.width, this.height, 64, 64);
        } else {
            blit(matrix, this.x, this.y, 48f, 0f, this.width, this.height, 64, 64);
        }

        RenderSystem.enableDepthTest();
    }
}