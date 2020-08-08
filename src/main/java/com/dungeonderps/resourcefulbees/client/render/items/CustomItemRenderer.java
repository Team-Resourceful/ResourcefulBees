package com.dungeonderps.resourcefulbees.client.render.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BedTileEntity;

public class CustomItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        TileEntityRendererDispatcher.instance.renderItem(new BedTileEntity(), matrix, buffer, combinedLight, combinedOverlay);
    }
}
