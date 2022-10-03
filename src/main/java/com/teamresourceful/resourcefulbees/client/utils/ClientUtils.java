package com.teamresourceful.resourcefulbees.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public final class ClientUtils {

    private ClientUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void renderEntity(PoseStack stack, Entity entity, float x, float y, float rotation, float renderScale) {
        float scaledSize;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        if (entity instanceof CustomBeeEntity customBee) {
            scaledSize = 20 / customBee.getRenderData().sizeModifier();
        } else {
            scaledSize = 20 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        }
        if (mc.player != null) {
            try (var ignored = new CloseablePoseStack(stack)) {
                stack.translate(10, 20 * renderScale, 0.5);
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
        for (int i = 0; i < splits; i++)
            GuiComponent.blit(matrix,x, y + (i * 16), blitOffset, width, i+1 == splits && remainder != 0 ? remainder : 16, sprite);
    }

    //TODO swap all calls to this method to the method being wrapped
    public static void bindTexture(ResourceLocation location) {
        RenderUtils.bindTexture(location);
    }
}
