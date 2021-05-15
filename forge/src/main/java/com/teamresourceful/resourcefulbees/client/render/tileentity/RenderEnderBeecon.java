package com.teamresourceful.resourcefulbees.client.render.tileentity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.teamresourceful.resourcefulbees.utils.CubeModel;
import com.teamresourceful.resourcefulbees.utils.RenderCuboid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RenderEnderBeecon extends BlockEntityRenderer<EnderBeeconTileEntity> {

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    public RenderEnderBeecon(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(EnderBeeconTileEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getFluidTank().getFluid();
        long gameTime = tile.getLevel().getGameTime();
        List<EnderBeeconTileEntity.BeamSegment> list = tile.getBeamSegments();
        int currentHeight = 0;

        if (!stack.isEmpty()) {
            int level = tile.getFluidLevel();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            Vector3f start = new Vector3f(0.26f, 0.25f, 0.26f);
            Vector3f end = new Vector3f(0.74f, 0.25f + ((float) level / 100.0F) * 0.375f, 0.74f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
            if (!tile.isShowBeam()) return;
            float red = RenderCuboid.getRed(color);
            float green = RenderCuboid.getGreen(color);
            float blue = RenderCuboid.getBlue(color);
            float alpha = RenderCuboid.getAlpha(color);
            float[] afloats = {red, green, blue, alpha};
            for (int k = 0; k < list.size(); ++k) {
                EnderBeeconTileEntity.BeamSegment segment = list.get(k);
                BeaconRenderer.renderBeaconBeam(matrix, renderer, TEXTURE_BEACON_BEAM, partialTick, 1.0F, gameTime, currentHeight, k == list.size() - 1 ? 1024 : segment.getHeight(), afloats, 0.2F, 0.25F);
                currentHeight += segment.getHeight();
            }
        }
    }
}
