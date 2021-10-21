package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeActivity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockBlock;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCentrifuge extends RectangularMultiblockBlock<CentrifugeController, AbstractCentrifugeEntity, AbstractCentrifuge> {

    protected AbstractCentrifuge(@NotNull AbstractBlock.Properties properties) {
        super(properties);

        if (usesCentrifugeState()) {
            registerDefaultState(defaultBlockState().setValue(CentrifugeActivity.CENTRIFUGE_ACTIVITY_ENUM_PROPERTY, CentrifugeActivity.INACTIVE));
        }
    }


    @Override
    protected void createBlockStateDefinition(@NotNull StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        if (usesCentrifugeState()) {
            builder.add(CentrifugeActivity.CENTRIFUGE_ACTIVITY_ENUM_PROPERTY);
        }
    }

    public boolean usesCentrifugeState() {
        return false;
    }

    @Override
    public boolean isGoodForInterior() {
        return false;
    }

    @Override
    public boolean isGoodForExterior() {
        return true;
    }

    @Override
    public boolean isGoodForFrame() {
        return false;
    }
}
