package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

import java.util.logging.Level;

public class RenderUtils {

    private RenderUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(spriteLocation);
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
    public static void drawTiledSprite(PoseStack matrix, int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        Minecraft.getInstance().textureManager.bind(InventoryMenu.BLOCK_ATLAS);
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        BufferBuilder vertexBuffer = Tesselator.getInstance().getBuilder();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = matrix.last().pose();
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
                vertexBuffer.vertex(matrix4f, x, y + textureHeight, zLevel).uv(uMin, vMaxLocal).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + textureHeight, zLevel).uv(uMaxLocal, vMaxLocal).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + maskTop, zLevel).uv(uMaxLocal, vMin).endVertex();
                vertexBuffer.vertex(matrix4f, x, y + maskTop, zLevel).uv(uMin, vMin).endVertex();
            }
        }
        vertexBuffer.end();
        BufferUploader.end(vertexBuffer);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void renderFluid(PoseStack matrix, FluidTank fluidTank, int tankNumber, int xPos, int yPos, int width, int height, int zOffset) {
        FluidStack stack = fluidTank.getFluidInTank(tankNumber);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stack.getFluid().getAttributes().getStillTexture());
        int color = stack.getFluid().getAttributes().getColor();
        float red = RenderCuboid.getRed(color);
        float green = RenderCuboid.getGreen(color);
        float blue = RenderCuboid.getBlue(color);
        float alpha = RenderCuboid.getAlpha(color);
        int effectiveHeight = (int) (((float) stack.getAmount() / (float) fluidTank.getTankCapacity(tankNumber)) * height);
        //noinspection deprecation
        RenderSystem.color4f(red, green, blue, alpha);
        RenderUtils.drawTiledSprite(matrix, xPos, yPos, height, width, effectiveHeight, sprite, 16, 16, zOffset);
        resetColor();
    }

    public static void renderFluid(PoseStack matrix, FluidTank fluidTank, int xPos, int yPos, int width, int height, int zOffset) {
        renderFluid(matrix, fluidTank, 0, xPos, yPos, width, height, zOffset);
    }

    public static void renderFluid(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, int width, int height, int zOffset) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStack.getFluid().getAttributes().getStillTexture());
        int color = fluidStack.getFluid().getAttributes().getColor();
        float red = RenderCuboid.getRed(color);
        float green = RenderCuboid.getGreen(color);
        float blue = RenderCuboid.getBlue(color);
        float alpha = RenderCuboid.getAlpha(color);
        //noinspection deprecation
        RenderSystem.color4f(red, green, blue, alpha);
        RenderUtils.drawTiledSprite(matrix, xPos, yPos, height, width, height, sprite, 16, 16, zOffset);
        resetColor();
    }

    public static void renderFluid(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, int zOffset) {
        renderFluid(matrix, fluidStack, xPos, yPos, 16, 16, zOffset);
    }

    public static void renderEntity(PoseStack matrixStack, Entity entity, Level world, float x, float y, float rotation, float renderScale) {
        if (world == null) return;
        float scaledSize = 20;
        Minecraft mc = Minecraft.getInstance();
        if (entity instanceof LivingEntity) {
            if (mc.player != null) entity.tickCount = mc.player.tickCount;
            if (entity instanceof CustomBeeEntity) {
                scaledSize = 20 / ((CustomBeeEntity) entity).getBeeData().getSizeModifier();
            } else {
                scaledSize = 20 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
            }
        }
        if (mc.player != null) {
            matrixStack.pushPose();
            matrixStack.translate(10, 20 * renderScale, 0.5);
            matrixStack.translate(x, y, 1);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            matrixStack.translate(0, 0, 1);
            matrixStack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
            EntityRenderDispatcher entityrenderermanager = mc.getEntityRenderDispatcher();
            MultiBufferSource.BufferSource renderTypeBuffer = mc.renderBuffers().bufferSource();
            entityrenderermanager.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, matrixStack, renderTypeBuffer, 15728880);
            renderTypeBuffer.endBatch();
        }
        matrixStack.popPose();
    }
}
