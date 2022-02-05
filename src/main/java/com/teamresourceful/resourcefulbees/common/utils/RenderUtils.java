package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.FontResourceManagerAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class RenderUtils {

    public static final int FONT_COLOR_1 = 0xffc9c9c9;
    public static final int FONT_COLOR_2 = 0xff2cafc7;
    private static final FontSet FONT_8 =  ((FontResourceManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(ResourcefulBees.MOD_ID, "jetbrains_mono_8"));
    public static final Font TERMINAL_FONT_8 = new Font(resourceLocation -> FONT_8);
    private static final FontSet FONT_12 =  ((FontResourceManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(ResourcefulBees.MOD_ID, "jetbrains_mono_12"));
    public static final Font TERMINAL_FONT_12 = new Font(resourceLocation -> FONT_12);

    private RenderUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void resetColor() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * @author Pupnewfster
     * implNote Lightly modified method borrowed from Mekanism to draw fluids in tank since I'm still learning Render stuff
     */
    public static void drawTiledSprite(PoseStack matrix, int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        bindTexture(InventoryMenu.BLOCK_ATLAS);
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
        //TODO RenderSystem.enableAlphaTest();
        BufferBuilder vertexBuffer = Tesselator.getInstance().getBuilder();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
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
        //TODO RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void renderFluid(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, int width, int height, int zOffset) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStack.getFluid().getAttributes().getStillTexture());
        int color = fluidStack.getFluid().getAttributes().getColor();
        float red = RenderCuboid.getRed(color);
        float green = RenderCuboid.getGreen(color);
        float blue = RenderCuboid.getBlue(color);
        float alpha = RenderCuboid.getAlpha(color);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderUtils.drawTiledSprite(matrix, xPos, yPos, height, width, height, sprite, 16, 16, zOffset);
        resetColor();
    }

    public static void renderFluid(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, int zOffset) {
        renderFluid(matrix, fluidStack, xPos, yPos, 16, 16, zOffset);
    }

    public static void renderEntity(PoseStack matrixStack, Entity entity, Level world, float x, float y, float rotation, float renderScale) {
        if (world == null) return;
        float scaledSize;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        if (entity instanceof CustomBeeEntity customBee) {
            scaledSize = 20 / customBee.getRenderData().sizeModifier();
        } else {
            scaledSize = 20 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        }
        if (mc.player != null) {
            matrixStack.pushPose();
            matrixStack.translate(10, 20 * renderScale, 0.5);
            matrixStack.translate(x, y, 1);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            matrixStack.translate(0, 0, 100);
            matrixStack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
            EntityRenderDispatcher entityrenderermanager = mc.getEntityRenderDispatcher();
            MultiBufferSource.BufferSource renderTypeBuffer = mc.renderBuffers().bufferSource();
            entityrenderermanager.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, matrixStack, renderTypeBuffer, 15728880);
            renderTypeBuffer.endBatch();
        }
        matrixStack.popPose();
    }

    public static void drawFluid(PoseStack matrix, int height, int width, FluidStack fluidStack, int x, int y, int blitOffset) {
        Minecraft mc = Minecraft.getInstance();
        bindTexture(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStack.getFluid().getAttributes().getStillTexture());
        int remainder = height % 16;
        int splits = (height - remainder) / 16;
        if (remainder != 0) splits++;
        int fluidColor = fluidStack.getFluid().getAttributes().getColor();

        RenderSystem.setShaderColor(((fluidColor >> 16) & 0xFF)/ 255.0F, ((fluidColor >> 8) & 0xFF)/ 255.0F, (fluidColor & 0xFF)/ 255.0F,  ((fluidColor >> 24) & 0xFF)/ 255.0F);
        for (int i = 0; i < splits; i++)
            GuiComponent.blit(matrix,x, y + (i * 16), blitOffset, width, i+1 == splits && remainder != 0 ? remainder : 16, sprite);
    }

    public static void bindTexture(ResourceLocation location) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, location);
    }
}
