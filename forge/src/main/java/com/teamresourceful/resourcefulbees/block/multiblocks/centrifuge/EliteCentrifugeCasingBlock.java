package com.teamresourceful.resourcefulbees.block.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeCasingTileEntity;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EliteCentrifugeCasingBlock extends CentrifugeCasingBlock {
    public EliteCentrifugeCasingBlock(Properties properties) { super(properties); }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new EliteCentrifugeCasingTileEntity(ModBlockEntityTypes.ELITE_CENTRIFUGE_CASING_ENTITY.get());
    }

    @Override
    protected CentrifugeControllerTileEntity getControllerEntity(Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof EliteCentrifugeCasingTileEntity) {
            return ((EliteCentrifugeCasingTileEntity) tileEntity).getController();
        }
        return null;
    }
}
