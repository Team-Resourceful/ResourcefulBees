package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.FluidUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class SolidificationChamberBlock extends TickingBlock<SolidificationChamberBlockEntity> {

    protected static final VoxelShape VOXEL_SHAPE = Util.make(() -> {
        VoxelShape shape = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 5.0D, 15.0D);
        shape = Shapes.join(shape, Block.box(6.0D, 5.0D, 1.0D, 10.0D, 9.0D, 15.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(1.0D, 5.0D, 6.0D, 15.0D, 9.0D, 10.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(3.0D, 5.0D, 3.0D, 13.0D, 16.0D, 13.0D), BooleanOp.OR);
        return shape;
    });

    public SolidificationChamberBlock(Properties properties) {
        super(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY, properties);
    }

    private static SolidificationChamberBlockEntity getBlockEntity(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SolidificationChamberBlockEntity entity ? entity : null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof SolidificationChamberBlockEntity chamber) {
            if (!level.isClientSide) {
                FluidUtils.checkBottleAndCapability(chamber.getFluidContainer(), chamber, player, level, pos, hand);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        SolidificationChamberBlockEntity tank = getBlockEntity(level, pos);
        if (tank == null) return;
        if (tank.getFluid().getFluid() instanceof CustomHoneyFluid fluid && fluid.getHoneyData().renderData().color().isRainbow()) {
            level.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        }
        super.animateTick(stateIn, level, pos, rand);
    }


    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VOXEL_SHAPE;
    }
}
