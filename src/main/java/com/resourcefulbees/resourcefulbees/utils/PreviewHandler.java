package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.block.ApiaryBlock;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.ApiaryTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.ArrayList;
import java.util.List;

public class PreviewHandler {
    private static BlockPos apiaryPos = null;
    private static List<BlockPos> STRUCTURE_PREVIEW_POS = new ArrayList<>();
    private static boolean enabled;

    public static void setPreview(BlockPos apiary, MutableBoundingBox box, boolean enabled){
        STRUCTURE_PREVIEW_POS.clear();
        if (enabled) {
            PreviewHandler.apiaryPos = apiary;
            BlockPos.stream(box).forEach((blockPos -> {
                if (blockPos.getX() == box.minX || blockPos.getX() == box.maxX ||
                        blockPos.getY() == box.minY || blockPos.getY() == box.maxY ||
                        blockPos.getZ() == box.minZ || blockPos.getZ() == box.maxZ) {
                    if (!apiary.equals(blockPos.toImmutable())) {
                        BlockPos savedPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        STRUCTURE_PREVIEW_POS.add(savedPos);
                    }
                }
            }));
        }
        else apiaryPos = null;
        PreviewHandler.enabled = enabled;
    }

    public static void onWorldRenderLast(RenderWorldLastEvent event) {
        World world = Minecraft.getInstance().world;
        MatrixStack ms = event.getMatrixStack();

        IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().getBufferBuilders().getEntityVertexConsumers();
        IVertexBuilder buffer = buffers.getBuffer(RenderType.getTranslucent());

        if (apiaryPos != null && enabled) {
            if (world != null && world.getBlockState(apiaryPos).getBlock() instanceof ApiaryBlock)
                if (world.getTileEntity(apiaryPos) instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTE = (ApiaryTileEntity) world.getTileEntity(apiaryPos);
                    if (apiaryTE != null && apiaryTE.previewed)
                        for (BlockPos pos : STRUCTURE_PREVIEW_POS) {
                            if (world.getBlockState(pos).equals(Blocks.AIR.getDefaultState()))
                                renderBlockAt(ms, buffer, RegistryHandler.PREVIEW_BLOCK.get().getDefaultState(), pos, 0xffffff);
                            else {
                                if (BeeInfoUtils.getBlockTag("resourcefulbees:valid_apiary") != null)
                                    if (!world.getBlockState(pos).isIn(BeeInfoUtils.getBlockTag("resourcefulbees:valid_apiary"))) {
                                        renderBlockAt(ms, buffer, RegistryHandler.ERRORED_PREVIEW_BLOCK.get().getDefaultState(), pos, 0xffffff);
                                    }
                            }
                        }
                }
            else {
                STRUCTURE_PREVIEW_POS.clear();
                apiaryPos = null;
                PreviewHandler.enabled = false;
            }
        }

        buffers.draw(RenderType.getTranslucent());
    }

    private static void renderBlockAt(MatrixStack ms, IVertexBuilder buffer, BlockState state, BlockPos pos, int lightIn) {
        double renderPosX = Minecraft.getInstance().getRenderManager().info.getProjectedView().getX();
        double renderPosY = Minecraft.getInstance().getRenderManager().info.getProjectedView().getY();
        double renderPosZ = Minecraft.getInstance().getRenderManager().info.getProjectedView().getZ();

        ms.push();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();
        ms.translate(pos.getX(), pos.getY(), pos.getZ());
        IBakedModel model = brd.getModelForState(state);
        int color = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        brd.getBlockModelRenderer().renderModel(ms.peek(), buffer, state, model, r, g, b, lightIn, OverlayTexture.DEFAULT_UV, EmptyModelData.INSTANCE);

        ms.pop();
    }
}
