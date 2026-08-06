package com.teamresourceful.resourcefulbees.client.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.common.caches.CacheableBiFunction;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicInteger;

public final class ClientRenderUtils {

    private ClientRenderUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final AtomicInteger NEXT_PREVIEW_ENTITY_ID = new AtomicInteger(-1);

    public static final CacheableBiFunction<Identifier, Identifier, Identifier> DEFAULT_TEXTURER = new CacheableBiFunction<>((texture, other) -> texture == other ? texture : Minecraft.getInstance().getResourceManager().getResource(texture).isPresent() ? texture : other);

   /* public static void renderEntity(GuiGraphics graphics, Entity entity, float x, float y, float rotation, float renderScale) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        float scaledSize = 15 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (mc.player != null) {
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(10, 15 * renderScale, 0.5);
                pose.translate(x, y, 1);
                pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
                pose.translate(0, 0, 100);
                pose.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
                pose.mulPose(Axis.YP.rotationDegrees(rotation));
                EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
                MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
                entityRenderer.render(entity, 0, 0, 0.0D, mc.getTimer().getGameTimeDeltaPartialTick(true), 1, pose, buffer, LightTexture.FULL_BRIGHT);
                buffer.endBatch();
            }
        }
    }*/

    public static void renderEntity(
            GuiGraphicsExtractor graphics,
            Entity entity,
            int x,
            int y,
            int width,
            int height,
            float rotation,
            float renderScale
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || width <= 0 || height <= 0) {
            return;
        }

        /*
         * GUI-only entities are not added to the level, so they do not receive
         * a normal runtime entity ID. Render-state extraction still requires one.
         */
        try {
            entity.getId();
        } catch (IllegalStateException _) {
            entity.setId(NEXT_PREVIEW_ENTITY_ID.getAndDecrement());
        }

        entity.tickCount = minecraft.player.tickCount;

        float entitySize = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if (entitySize <= 0.0F) {
            return;
        }

        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        EntityRenderState renderState = minecraft.getEntityRenderDispatcher().extractEntity(entity, partialTick);

        /*
         * Scale is expressed in GUI pixels per model unit.
         *
         * Use the available GUI area rather than a fixed value of 15.
         */
        float availableSize = Math.min(width, height);
        float scale = availableSize / entitySize * renderScale;

        /*
         * Model-space offset inside the GUI rectangle.
         *
         * Move the entity upward by half of its bounding-box height so its
         * center is located in the preview rectangle.
         */
        Vector3f translation = new Vector3f(0.0F, entity.getBbHeight() / 2.0F, 0.0F);

        Quaternionf modelRotation = new Quaternionf()
                .rotateZ((float) Math.PI)
                .rotateY((float) Math.toRadians(rotation));

        graphics.entity(renderState, scale, translation, modelRotation, null, x, y, x + width, y + height);
    }


    public static void drawTank(GuiGraphicsExtractor graphics, ResourceHandler<FluidResource> resourceHandler, int tank, int x, int y, int width, int height) {
        if (tank < 0 || tank >= resourceHandler.size()) return;
        FluidResource resource = resourceHandler.getResource(tank);
        if (resource.isEmpty()) return;

        long amount = resourceHandler.getAmountAsLong(tank);
        long capacity = resourceHandler.getCapacityAsLong(tank, resource);

        if (amount <= 0 || capacity <= 0) return;

        int fluidHeight = (int) Math.clamp(amount * height / capacity, 1L, height);

        Fluid fluid = resource.getFluid();

        FluidModel fluidModel = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(fluid.defaultFluidState());

        TextureAtlasSprite sprite = fluidModel
                .stillMaterial()
                .sprite();

        int tint = 0xFFFFFFFF;

        drawTiledSprite(
                graphics,
                sprite,
                tint,
                x,
                y + height - fluidHeight,
                width,
                fluidHeight
        );
    }

    private static void drawTiledSprite(GuiGraphicsExtractor graphics, TextureAtlasSprite sprite, int color, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        final int tileSize = 16;
        final int right = x + width;
        final int bottom = y + height;

        graphics.enableScissor(x, y, right, bottom);

        try {
            for (int tileY = bottom - tileSize;
                 tileY + tileSize > y;
                 tileY -= tileSize) {

                for (int tileX = x; tileX < right; tileX += tileSize) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, tileX, tileY, tileSize, tileSize, color);
                }
            }
        } finally {
            graphics.disableScissor();
        }
    }
}
