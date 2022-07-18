package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
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
        super(xIn, yIn, 12, 12, Component.empty(), onPressIn);
        switch (direction) {
            case UP -> xTexStart = 0;
            case DOWN -> xTexStart = 12;
            case LEFT -> xTexStart = 24;
            default -> xTexStart = 36;
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(arrowButtonTexture);
        RenderSystem.disableDepthTest();
        if (this.active) {
            blit(matrix, this.x, this.y, this.xTexStart, this.isMouseOver(mouseX, mouseY) ? 12f : 0f, this.width, this.height, 64, 64);
        } else {
            blit(matrix, this.x, this.y, 48f, 0f, this.width, this.height, 64, 64);
        }

        RenderSystem.enableDepthTest();
    }
}