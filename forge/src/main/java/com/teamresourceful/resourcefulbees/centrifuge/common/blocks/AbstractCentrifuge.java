package com.teamresourceful.resourcefulbees.centrifuge.common.blocks;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeActivity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.roguelogix.phosphophyllite.modular.block.PhosphophylliteBlock;
import net.roguelogix.phosphophyllite.multiblock2.rectangular.IRectangularMultiblockBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCentrifuge extends PhosphophylliteBlock implements IRectangularMultiblockBlock {

    protected AbstractCentrifuge(BlockBehaviour.Properties properties) {
        super(properties);

        if (usesCentrifugeState()) {
            registerDefaultState(defaultBlockState().setValue(CentrifugeActivity.PROPERTY, CentrifugeActivity.INACTIVE));
        }
    }

    @Override
    protected void buildStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        if (usesCentrifugeState()) {
            builder.add(CentrifugeActivity.PROPERTY);
        }
    }

    @Override
    public void onPlaced(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, LivingEntity pPlacer, @NotNull ItemStack pStack) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof AbstractGUICentrifugeEntity abstractEntity) {
            if (pStack.hasCustomHoverName()) {
                abstractEntity.setCustomName(pStack.getHoverName());
            }
            abstractEntity.setOwner(pPlacer.getDisplayName().getString());
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

    @Override
    public abstract void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag);
}
