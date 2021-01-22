package com.resourcefulbees.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.utils.CubeModel;
import com.resourcefulbees.resourcefulbees.utils.RenderCuboid;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class RenderEnderBeecon extends TileEntityRenderer<EnderBeeconTileEntity> {

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    public RenderEnderBeecon(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(EnderBeeconTileEntity tile, float partialTick, MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight) {
        if (tile.getWorld() == null) return;
        FluidStack stack = tile.fluidTank.getFluid();
        long gameTime = tile.getWorld().getGameTime();
        List<EnderBeeconTileEntity.BeamSegment> list = tile.getBeamSegments();
        int currentHeight = 0;

        if (stack != null && !stack.isEmpty()) {
            int level = tile.getLevel();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            IVertexBuilder builder = renderer.getBuffer(Atlases.getEntityTranslucentCull());
            Vector3f start = new Vector3f(0.26f, 0.25f, 0.26f);
            Vector3f end = new Vector3f(0.74f, 0.25f + ((float) level / 100.0F) * 0.375f, 0.74f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
            float red = RenderCuboid.INSTANCE.getRed(color);
            float green = RenderCuboid.INSTANCE.getGreen(color);
            float blue = RenderCuboid.INSTANCE.getBlue(color);
            float alpha = RenderCuboid.INSTANCE.getAlpha(color);
            float[] afloats = {red, green, blue, alpha};
            for (int k = 0; k < list.size(); ++k) {
                EnderBeeconTileEntity.BeamSegment segment = list.get(k);
                BeaconTileEntityRenderer.renderLightBeam(matrix, renderer, TEXTURE_BEACON_BEAM, partialTick, 1.0F, gameTime, currentHeight, k == list.size() - 1 ? 1024 : segment.getHeight(), afloats, 0.2F, 0.25F);
                currentHeight += segment.getHeight();
            }
        }
    }
}
