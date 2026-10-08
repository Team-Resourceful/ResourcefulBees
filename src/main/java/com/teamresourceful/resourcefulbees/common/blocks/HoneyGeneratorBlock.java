package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.MenuBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.NonNull;

public class HoneyGeneratorBlock extends TickingBlock<HoneyGeneratorBlockEntity> implements MenuBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE_PROPERTY = BooleanProperty.create("active");

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(4, 4, 13, 12, 12, 16),
            Block.box(10, 5, 0, 16, 13, 12),
            Block.box(11, 4, 1, 15, 5, 3),
            Block.box(11, 4, 9, 15, 5, 11),
            Block.box(1, 4, 1, 8, 5, 3),
            Block.box(1, 4, 9, 8, 5, 11),
            Block.box(0, 5, 0, 9, 16, 12),
            Block.box(9, 6, 1, 11, 8, 3),
            Block.box(5, 14, 12, 6, 15, 14),
            Block.box(7, 14, 12, 8, 15, 14),
            Block.box(5, 12, 13, 6, 14, 14),
            Block.box(7, 12, 13, 8, 14, 14)
    );

    private static final VoxelShape SHAPE_EAST = rotateY(SHAPE_NORTH);

    private static final VoxelShape SHAPE_SOUTH = rotateY(SHAPE_EAST);

    private static final VoxelShape SHAPE_WEST = rotateY(SHAPE_SOUTH);

    private static VoxelShape rotateY(VoxelShape shape) {
        VoxelShape[] result = {Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                result[0] = Shapes.or(result[0], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX)));
        return result[0];
    }

    public HoneyGeneratorBlock(Properties properties) {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY, properties);
        registerDefaultState(defaultBlockState().setValue(ACTIVE_PROPERTY, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        return MenuBlock.super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HoneyGeneratorBlockEntity honeyGen) {
            boolean moved = FluidUtil.interactWithFluidHandler(player, hand, pos, honeyGen.tank(), null);
            if (moved) {
                return InteractionResult.SUCCESS_SERVER;
            }
            return FluidUtils.fillOrEmptyBottle(honeyGen.tank(), player, hand);
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE_PROPERTY, FACING);
    }
}
