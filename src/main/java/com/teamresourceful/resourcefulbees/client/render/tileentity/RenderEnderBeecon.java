package com.teamresourceful.resourcefulbees.client.render.tileentity;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.common.block.EnderBeecon;
import com.teamresourceful.resourcefulbees.common.tileentity.EnderBeeconTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.CubeModel;
import com.teamresourceful.resourcefulbees.common.utils.RenderCuboid;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderEnderBeecon extends TileEntityRenderer<EnderBeeconTileEntity> {

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    public RenderEnderBeecon(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(EnderBeeconTileEntity tile, float partialTick, @NotNull MatrixStack matrix, @NotNull IRenderTypeBuffer renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getTank().getFluid();
        long gameTime = tile.getLevel().getGameTime();

        if (!stack.isEmpty()) {

            BlockState state = tile.getBlockState();
            boolean showBeam = state.hasProperty(EnderBeecon.BEAM) && state.getValue(EnderBeecon.BEAM);

            // render tank
            float percentage = tile.getTank().getFluidAmount() / (float)tile.getTank().getCapacity();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            IVertexBuilder builder = renderer.getBuffer(Atlases.translucentCullBlockSheet());
            Vector3f start = new Vector3f(0.26f, 0.25f, 0.26f);
            Vector3f end = new Vector3f(0.74f, 0.25f + percentage * 0.375f, 0.74f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
            // render beam
            if (!showBeam) return;
            float red = RenderCuboid.getRed(color);
            float green = RenderCuboid.getGreen(color);
            float blue = RenderCuboid.getBlue(color);
            float alpha = RenderCuboid.getAlpha(color);
            float[] afloats = {red, green, blue, alpha};
            BeaconTileEntityRenderer.renderBeaconBeam(matrix, renderer, TEXTURE_BEACON_BEAM, partialTick, 1.0F, gameTime, 0, 1024, afloats, 0.2F, 0.25F);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull EnderBeeconTileEntity tile) {
        return true;
    }
}
