package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
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

    public ArrowButton(int xIn, int yIn, Direction direction, OnPress onPressIn) {
        super(xIn, yIn, 12, 12, TextComponent.EMPTY, onPressIn);
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
    public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
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