package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.MenuBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SolidificationChamberBlock extends TickingBlock<SolidificationChamberBlockEntity> implements MenuBlock {

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 4, 3, 13, 5, 13),
            Block.box(1, 0, 1, 15, 4, 15),
            Block.box(3, 5, 3, 13, 16, 13)
    );

    public SolidificationChamberBlock(Properties properties) {
        super(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY, properties);
    }

    private static SolidificationChamberBlockEntity getBlockEntity(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SolidificationChamberBlockEntity entity ? entity : null;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        SolidificationChamberBlockEntity tank = getBlockEntity(level, pos);
        if (tank == null) return;
        if (tank.fluidResource().getFluid() instanceof CustomHoneyFluid.Still fluid && fluid.getHoneyFluidData().renderData().color().isSpecial()) {
            level.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        }
        super.animateTick(stateIn, level, pos, rand);
    }


    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        return MenuBlock.super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SolidificationChamberBlockEntity chamber) {
            boolean moved = FluidUtil.interactWithFluidHandler(player, hand, pos, chamber.tank(), null);
            if (moved) {
                return InteractionResult.SUCCESS_SERVER;
            }
            return FluidUtils.fillOrEmptyBottle(chamber.tank(), player, hand);
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }
}
