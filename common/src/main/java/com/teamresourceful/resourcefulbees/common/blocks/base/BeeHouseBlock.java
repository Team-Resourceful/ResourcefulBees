package com.teamresourceful.resourcefulbees.common.blocks.base;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@SuppressWarnings("deprecation")
public abstract class BeeHouseBlock extends RenderingBaseEntityBlock {

    private static final VoxelShape FULL_Z_SHAPE = Stream.of(
            Block.box(1, 0, 1, 15, 13, 15),
            Block.box(0, 13, 0, 16, 16, 16),
            Block.box(1, 16, 0, 15, 18, 16),
            Block.box(3, 18, 0, 13, 20, 16),
            Block.box(5, 20, 0, 11, 22, 16),
            Block.box(7, 22, -1, 9, 24, 17)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape FULL_X_SHAPE = Stream.of(
            Block.box(0, 16, 1, 16, 18, 15),
            Block.box(0, 13, 0, 16, 16, 16),
            Block.box(1, 0, 1, 15, 13, 15),
            Block.box(0, 18, 3, 16, 20, 13),
            Block.box(0, 20, 5, 16, 22, 11),
            Block.box(-1, 22, 7, 17, 24, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected BeeHouseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockRayTraceResult) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            MenuProvider blockEntity = state.getMenuProvider(world,pos);
            if (blockEntity != null) {
                ModUtils.openScreen(player, blockEntity, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockState topState = level.getBlockState(pos.above());
        return topState.getMaterial().isReplaceable() || topState.getBlock() instanceof BeeHouseTopBlock;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), ModBlocks.BEEHOUSE_TOP.get().withPropertiesOf(state), Block.UPDATE_ALL);
    }

    //We make this public so the top block can call this.
    @Override
    public void spawnDestroyParticles(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.spawnDestroyParticles(level, player, pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        }
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.hasProperty(HORIZONTAL_FACING) && state.getValue(HORIZONTAL_FACING).getAxis().equals(Direction.Axis.Z) ? FULL_Z_SHAPE : FULL_X_SHAPE;
    }
}
