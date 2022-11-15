package com.teamresourceful.resourcefulbees.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.caches.CacheableBiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public final class ClientUtils {

    public static final CacheableBiFunction<ResourceLocation, ResourceLocation, ResourceLocation> DEFAULT_TEXTURER = new CacheableBiFunction<>((texture, other) -> texture == other ? texture : Minecraft.getInstance().getResourceManager().getResource(texture).isPresent() ? texture : other);

    private ClientUtils() {
        throw new UtilityClassError();
    }

    public static void renderEntity(PoseStack stack, Entity entity, float x, float y, float rotation, float renderScale) {
        float scaledSize;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        scaledSize = 15 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (mc.player != null) {
            try (var ignored = new CloseablePoseStack(stack)) {
                stack.translate(10, 15 * renderScale, 0.5);
                stack.translate(x, y, 1);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                stack.translate(0, 0, 100);
                stack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
                stack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
                MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
                entityRenderer.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, stack, buffer, LightTexture.FULL_BRIGHT);
                buffer.endBatch();
            }
        }
    }

    public static void drawFluid(PoseStack matrix, int height, int width, FluidStack fluidStack, int x, int y, int blitOffset) {
        Minecraft mc = Minecraft.getInstance();
        bindTexture(InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture(fluidStack));
        int remainder = height % 16;
        int splits = (height - remainder) / 16;
        if (remainder != 0) splits++;
        int fluidColor = props.getTintColor(fluidStack);

        RenderSystem.setShaderColor(((fluidColor >> 16) & 0xFF)/ 255.0F, ((fluidColor >> 8) & 0xFF)/ 255.0F, (fluidColor & 0xFF)/ 255.0F,  ((fluidColor >> 24) & 0xFF)/ 255.0F);
        for (int i = 0; i < splits; i++) {
            //TODO figure out why they are still squished
            int splitHeight = (i + 1 == splits && remainder != 0 ? remainder : 16);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix.last().pose(), x, y + (i * 16) + splitHeight, blitOffset).uv(sprite.getU0(), sprite.getV(width)).endVertex();
            bufferbuilder.vertex(matrix.last().pose(), x + width, y + (i * 16) + splitHeight, blitOffset).uv(sprite.getU(splitHeight), sprite.getV(width)).endVertex();
            bufferbuilder.vertex(matrix.last().pose(), x + width, y + (i * 16), blitOffset).uv(sprite.getU(splitHeight), sprite.getV0()).endVertex();
            bufferbuilder.vertex(matrix.last().pose(), x, y + (i * 16), blitOffset).uv(sprite.getU0(), sprite.getV0()).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
        }
    }

    //TODO swap all calls to this method to the method being wrapped
    public static void bindTexture(ResourceLocation location) {
        RenderUtils.bindTexture(location);
    }

    public static void onResourceReload(ModelEvent.BakingCompleted event) {
        DEFAULT_TEXTURER.clear();
    }
}
