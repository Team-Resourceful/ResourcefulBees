package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderUtils {

    private RenderUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getModelManager().method_24153(PlayerContainer.BLOCK_ATLAS_TEXTURE).getSprite(spriteLocation);
    }

    public static TextureAtlasSprite getStillFluidTexture(FluidStack fluidStack) {
        return getSprite(fluidStack.getFluid().getAttributes().getStillTexture(fluidStack));
    }

    public static void resetColor() {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * @author Pupnewfster
     * implNote Lightly modified method borrowed from Mekanism to draw fluids in tank since I'm still learning Render stuff
     */
    public static void drawTiledSprite(MatrixStack matrix, int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        Minecraft.getInstance().textureManager.bindTexture(PlayerContainer.BLOCK_ATLAS_TEXTURE);
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getMinU();
        float uMax = sprite.getMaxU();
        float vMin = sprite.getMinV();
        float vMax = sprite.getMaxV();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        Matrix4f matrix4f = matrix.peek().getModel();
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uMaxLocal = uMax - (uDif * maskRight / textureWidth);
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    break;
                }
                float y = yStart - ((yTile + 1F) * textureHeight);
                int maskTop = textureHeight - height;
                float vMaxLocal = vMax - (vDif * maskTop / textureHeight);
                vertexBuffer.vertex(matrix4f, x, y + textureHeight, zLevel).texture(uMin, vMaxLocal).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + textureHeight, zLevel).texture(uMaxLocal, vMaxLocal).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + maskTop, zLevel).texture(uMaxLocal, vMin).endVertex();
                vertexBuffer.vertex(matrix4f, x, y + maskTop, zLevel).texture(uMin, vMin).endVertex();
            }
        }
        vertexBuffer.finishDrawing();
        WorldVertexBufferUploader.draw(vertexBuffer);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }
}
