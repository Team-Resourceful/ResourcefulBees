package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeActivity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockBlock;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCentrifuge extends RectangularMultiblockBlock<CentrifugeController, AbstractCentrifugeEntity, AbstractCentrifuge> {

    protected AbstractCentrifuge(@NotNull AbstractBlock.Properties properties) {
        super(properties);

        if (usesCentrifugeState()) {
            registerDefaultState(defaultBlockState().setValue(CentrifugeActivity.PROPERTY, CentrifugeActivity.INACTIVE));
        }
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        if (usesCentrifugeState()) {
            builder.add(CentrifugeActivity.PROPERTY);
        }
    }


    @Override
    public void setPlacedBy(@NotNull World pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, LivingEntity pPlacer, @NotNull ItemStack pStack) {
        TileEntity tileentity = pLevel.getBlockEntity(pPos);
        if (tileentity instanceof AbstractGUICentrifugeEntity) {
            if (pStack.hasCustomHoverName()) {
                ((AbstractGUICentrifugeEntity)tileentity).setCustomName(pStack.getHoverName());
            }
            ((AbstractGUICentrifugeEntity)tileentity).setOwner(pPlacer.getDisplayName().getString());
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
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
