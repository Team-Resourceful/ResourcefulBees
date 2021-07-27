package com.teamresourceful.resourcefulbees.compat.top;

import com.teamresourceful.resourcefulbees.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.tileentity.CentrifugeTileEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CentrifugeDisplayOverride implements IBlockDisplayOverride {

    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity tileEntity = world.getBlockEntity(iProbeHitData.getPos());
        return tileEntity instanceof CentrifugeTileEntity && createCentrifugeProbeData(iProbeInfo, blockState, (CentrifugeTileEntity) tileEntity);
    }

    @SuppressWarnings("SameReturnValue")
    private boolean createCentrifugeProbeData(IProbeInfo probeInfo, BlockState blockState, CentrifugeTileEntity tileEntity) {
        probeInfo.horizontal()
                .item(new ItemStack(blockState.getBlock().asItem()))
                .vertical()
                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        for (int i = 0; i < tileEntity.getNumberOfInputs(); i++) {
            if (tileEntity.getProcessTime(i) > 0) {
                probeInfo.horizontal()
                        .vertical()
                        .progress(getProcessTime(tileEntity, i), getRecipeTime(tileEntity, i));
            }
        }
        return true;
    }

    private int getRecipeTime(CentrifugeTileEntity tileEntity, int i) {
        return tileEntity.getRecipeTime(i) / 20;
    }

    private int getProcessTime(CentrifugeTileEntity tileEntity, int i) {
        return (int) Math.floor(tileEntity.getProcessTime(i) / 20.0);
    }
}
