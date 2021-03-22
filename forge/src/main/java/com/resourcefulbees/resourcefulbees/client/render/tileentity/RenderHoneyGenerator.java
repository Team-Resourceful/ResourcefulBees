package com.resourcefulbees.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
import com.resourcefulbees.resourcefulbees.utils.CubeModel;
import com.resourcefulbees.resourcefulbees.utils.RenderCuboid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderHoneyGenerator extends BlockEntityRenderer<HoneyGeneratorTileEntity> {

    public RenderHoneyGenerator(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(HoneyGeneratorTileEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.fluidTank.getFluid();
        if (!stack.isEmpty()) {
            int level = tile.getFluidLevel();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            Vector3f start = new Vector3f(0.0625f, 0.0625f, 0.0625f);
            Vector3f end = new Vector3f(0.9375f, 0.0625f + ((float) level / 100.0F) * 0.875f, 0.9375f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
        }
    }
}
