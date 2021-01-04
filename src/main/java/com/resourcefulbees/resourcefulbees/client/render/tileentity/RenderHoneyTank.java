package com.resourcefulbees.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

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
            IVertexBuilder builder = renderer.getBuffer(RenderType.getTranslucent());

        }
    }
}
