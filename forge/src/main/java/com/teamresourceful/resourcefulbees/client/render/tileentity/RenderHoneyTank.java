package com.teamresourceful.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.teamresourceful.resourcefulbees.utils.CubeModel;
import com.teamresourceful.resourcefulbees.utils.RenderCuboid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderHoneyTank extends BlockEntityRenderer<HoneyTankTileEntity> {

    public RenderHoneyTank(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(HoneyTankTileEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getFluidTank().getFluid();
        if (!stack.isEmpty()) {
            int level = tile.getFluidLevel();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            Vector3f start = new Vector3f(0.1875f, 0.0625f, 0.1875f);
            Vector3f end = new Vector3f(0.8125f, 0.0625f + ((float) level / 100.0F) * 0.875f, 0.8125f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
        }
    }
}
