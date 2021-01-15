package com.resourcefulbees.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.CubeModel;
import com.resourcefulbees.resourcefulbees.utils.RenderCuboid;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;

public class RenderHoneyTank extends TileEntityRenderer<HoneyTankTileEntity> {

    public RenderHoneyTank(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(HoneyTankTileEntity tile, float partialTick, MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight) {
        if (tile.getWorld() == null) return;
        FluidStack stack = tile.fluidTank.getFluid();
        if (stack != null && !stack.isEmpty()) {
            int level = tile.getLevel();
            int color;
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            if (stack.getFluid() instanceof HoneyFlowingFluid) {
                HoneyFlowingFluid fluid = (HoneyFlowingFluid) stack.getFluid();
                if (fluid.getHoneyData().isRainbow()) {
                    color = RainbowColor.getRGB();
                } else {
                    color = fluid.getHoneyData().getHoneyColorInt();
                }
            } else {
                color = stack.getFluid().getAttributes().getColor();
            }
            IVertexBuilder builder = renderer.getBuffer(RenderType.getTranslucent());
            Vector3f start = new Vector3f(0.135f, 0.0725f, 0.135f);
            Vector3f end = new Vector3f(0.865f, 0.0625f + ((float) level / 14.0F) * 0.875f - 0.01f, 0.865f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
        }
    }
}
