package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
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

    public static void renderFluid(MatrixStack matrix, FluidTank fluidTank, int tankNumber, int xPos, int yPos, int width, int height, int zOffset) {
        FluidStack stack = fluidTank.getFluidInTank(tankNumber);
        TextureAtlasSprite sprite = Minecraft.getInstance().getSpriteAtlas(PlayerContainer.BLOCK_ATLAS_TEXTURE).apply(stack.getFluid().getAttributes().getStillTexture());
        int color = stack.getFluid().getAttributes().getColor();
        float red = RenderCuboid.getRed(color);
        float green = RenderCuboid.getGreen(color);
        float blue = RenderCuboid.getBlue(color);
        float alpha = RenderCuboid.getAlpha(color);
        int effectiveHeight = (int) (((float) stack.getAmount() / (float) fluidTank.getTankCapacity(tankNumber)) * height);
        //noinspection deprecation
        RenderSystem.color4f(red, green, blue, alpha);
        RenderUtils.drawTiledSprite(matrix, xPos, yPos, height, width, effectiveHeight, sprite, 16, 16, zOffset);
    }

    public static void renderFluid(MatrixStack matrix, FluidTank fluidTank, int xPos, int yPos, int width, int height, int zOffset) {
        renderFluid(matrix, fluidTank, 0, xPos, yPos, width, height, zOffset);
    }

    public static void renderFluid(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int width, int height, int zOffset) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getSpriteAtlas(PlayerContainer.BLOCK_ATLAS_TEXTURE).apply(fluidStack.getFluid().getAttributes().getStillTexture());
        int color = fluidStack.getFluid().getAttributes().getColor();
        float red = RenderCuboid.getRed(color);
        float green = RenderCuboid.getGreen(color);
        float blue = RenderCuboid.getBlue(color);
        float alpha = RenderCuboid.getAlpha(color);
        //noinspection deprecation
        RenderSystem.color4f(red, green, blue, alpha);
        RenderUtils.drawTiledSprite(matrix, xPos, yPos, height, width, height, sprite, 16, 16, zOffset);
    }

    public static void renderFluid(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int zOffset) {
        renderFluid(matrix, fluidStack, xPos, yPos, 16, 16, zOffset);
    }

    public static void renderEntity(MatrixStack matrixStack, Entity entity, World world, float x, float y, float rotation, float renderScale) {
        if (world == null) return;
        float scaledSize = 20;
        Minecraft mc = Minecraft.getInstance();
        if (entity instanceof LivingEntity) {
            if (mc.player != null) entity.ticksExisted = mc.player.ticksExisted;
            if (entity instanceof CustomBeeEntity) {
                scaledSize = 20 / ((CustomBeeEntity) entity).getBeeData().getSizeModifier();
            } else {
                scaledSize = 20 / (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
            }
        }
        if (mc.player != null) {
            matrixStack.push();
            matrixStack.translate(10, 20 * renderScale, 0.5);
            matrixStack.translate(x, y, 1);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
            matrixStack.translate(0, 0, 1);
            matrixStack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
            EntityRendererManager entityrenderermanager = mc.getRenderManager();
            IRenderTypeBuffer.Impl renderTypeBuffer = mc.getBufferBuilders().getEntityVertexConsumers();
            entityrenderermanager.render(entity, 0, 0, 0.0D, mc.getRenderPartialTicks(), 1, matrixStack, renderTypeBuffer, 15728880);
            renderTypeBuffer.draw();
        }
        matrixStack.pop();
    }
}
