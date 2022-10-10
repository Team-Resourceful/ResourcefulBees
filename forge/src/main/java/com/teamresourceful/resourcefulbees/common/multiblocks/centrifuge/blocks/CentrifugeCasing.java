package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeCasingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeCasing extends AbstractCentrifuge implements IAssemblyStateBlock {
    public CentrifugeCasing(@NotNull BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isGoodForFrame() {
        return true;
    }

    @Override
    public boolean isGoodForCorner() {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CentrifugeCasingEntity(pos, state);
    }
}
