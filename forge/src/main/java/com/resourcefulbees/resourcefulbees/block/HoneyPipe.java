package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.tileentity.HoneyPipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HoneyPipe extends Block {

    public static final EnumProperty<PipeState> NORTH = EnumProperty.create("north", PipeState.class);
    public static final EnumProperty<PipeState> SOUTH = EnumProperty.create("south", PipeState.class);
    public static final EnumProperty<PipeState> EAST = EnumProperty.create("east", PipeState.class);
    public static final EnumProperty<PipeState> WEST = EnumProperty.create("west", PipeState.class);
    public static final EnumProperty<PipeState> UP = EnumProperty.create("up", PipeState.class);
    public static final EnumProperty<PipeState> DOWN = EnumProperty.create("down", PipeState.class);


    private static final VoxelShape VOXEL_SHAPE_CENTER = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    private static final VoxelShape VOXEL_SHAPE_NORTH = Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 7.0D);
    private static final VoxelShape VOXEL_SHAPE_EAST = Block.box(9.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
    private static final VoxelShape VOXEL_SHAPE_SOUTH = Block.box(5.0D, 5.0D, 9.0D, 11.0D, 11.0D, 16.0D);
    private static final VoxelShape VOXEL_SHAPE_WEST = Block.box(0.0D, 5.0D, 5.0D, 7.0D, 11.0D, 11.0D);
    private static final VoxelShape VOXEL_SHAPE_UP = Block.box(5.0D, 9.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape VOXEL_SHAPE_DOWN = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 5.0D, 11.0D);


    public HoneyPipe(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(NORTH, PipeState.OUTPUT)
                .setValue(SOUTH, PipeState.OUTPUT)
                .setValue(EAST, PipeState.NONE)
                .setValue(WEST, PipeState.NONE)
                .setValue(DOWN, PipeState.NONE)
                .setValue(UP, PipeState.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        List<VoxelShape> shapes = new ArrayList<>();
        if (state.getValue(NORTH) != PipeState.NONE) {
            shapes.add(VOXEL_SHAPE_NORTH);
        }
        if (state.getValue(EAST) != PipeState.NONE) {
            shapes.add(VOXEL_SHAPE_EAST);
        }
        if (state.getValue(SOUTH) != PipeState.NONE) {
            shapes.add(VOXEL_SHAPE_SOUTH);
        }
        if (state.getValue(WEST) != PipeState.NONE) {
            shapes.add(VOXEL_SHAPE_WEST);
        }
        if (state.getValue(UP) != PipeState.NONE) {
            shapes.add(VOXEL_SHAPE_UP);
        }
        if (state.getValue(DOWN) != PipeState.NONE) {
            shapes.add(VOXEL_SHAPE_DOWN);
        }
        return Shapes.or(VOXEL_SHAPE_CENTER, shapes.toArray(new VoxelShape[0]));
    }



    @Override
    @NotNull
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        updateNetwork();
        return updateState(stateIn, facingPos, world, getState(facing), facing.getOpposite());
    }

    private void updateNetwork() {
        // todo get all connected pipes, collect outputs and send update to input pipes.
    }

    @Override
    @NotNull
    public InteractionResult use(@NotNull BlockState blockState, @NotNull Level world, @NotNull BlockPos blockPos, @NotNull Player playerEntity, @NotNull InteractionHand hand, @NotNull BlockHitResult blockRayTraceResult) {
        // todo allow user to switch state of pipe, from input and output (if tank connected) or none, limit to one input per pipe
        return super.use(blockState, world, blockPos, playerEntity, hand, blockRayTraceResult);
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new HoneyPipeTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public static EnumProperty<PipeState> getState(Direction facing) {
        switch (facing) {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case SOUTH:
                return SOUTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            default:
                return NORTH;
        }
    }

    private boolean canConnect(BlockState state) {
        return state.getBlock() instanceof HoneyPipe;
    }

    private boolean isTank(BlockPos facingPos, LevelAccessor world, Direction direction) {
        if (world.getBlockState(facingPos).getBlock() instanceof HoneyPipe) return false;
        BlockEntity entity = world.getBlockEntity(facingPos);
        return entity != null && entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).isPresent();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor world = context.getLevel();
        BlockState stateIn = defaultBlockState();
        BlockPos currentPos = context.getClickedPos();
        return updateBlockState(world, stateIn, currentPos);
    }

    private BlockState updateBlockState(LevelAccessor world, BlockState stateIn, BlockPos currentPos) {
        stateIn = updateState(stateIn, currentPos.above(), world, UP, Direction.DOWN);
        stateIn = updateState(stateIn, currentPos.below(), world, DOWN, Direction.UP);
        stateIn = updateState(stateIn, currentPos.east(), world, EAST, Direction.WEST);
        stateIn = updateState(stateIn, currentPos.west(), world, WEST, Direction.EAST);
        stateIn = updateState(stateIn, currentPos.south(), world, SOUTH, Direction.NORTH);
        stateIn = updateState(stateIn, currentPos.north(), world, NORTH, Direction.SOUTH);
        return stateIn;
    }

    private BlockState updateState(BlockState stateIn, BlockPos pos, LevelAccessor world, EnumProperty<PipeState> stateProperty, Direction direction) {
        if (isTank(pos, world, direction)) {
            if (stateIn.getValue(stateProperty) == PipeState.INPUT) {
                return stateIn;
            } else {
                return stateIn.setValue(stateProperty, PipeState.OUTPUT);
            }
        } else if (canConnect(world.getBlockState(pos))) {
            return stateIn.setValue(stateProperty, PipeState.CONNECTED);
        } else {
            return stateIn.setValue(stateProperty, PipeState.NONE);
        }
    }

    public enum PipeState implements StringRepresentable {
        CONNECTED("connected"),
        OUTPUT("output"),
        INPUT("input"),
        NONE("none");

        private final String name;

        PipeState(String name) {
            this.name = name;
        }

        public String toString() {
            return this.getSerializedName();
        }

        @NotNull
        public String getSerializedName() {
            return this.name;
        }
    }
}
