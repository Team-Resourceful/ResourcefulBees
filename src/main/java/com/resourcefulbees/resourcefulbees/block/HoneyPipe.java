package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.tileentity.HoneyPipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
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
        return VoxelShapes.or(VOXEL_SHAPE_CENTER, shapes.toArray(new VoxelShape[0]));
    }

    @Override
    @NotNull
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull IWorld world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        updateNetwork();
        return updateState(stateIn, facingPos, world, getState(facing), facing.getOpposite());
    }

    private void updateNetwork() {
        // todo get all connected pipes, collect outputs and send update to input pipes.
    }

    @Override
    @NotNull
    public ActionResultType use(@NotNull BlockState blockState, @NotNull World world, @NotNull BlockPos blockPos, @NotNull PlayerEntity playerEntity, @NotNull Hand hand, @NotNull BlockRayTraceResult blockRayTraceResult) {
        // todo allow user to switch state of pipe, from input and output (if tank connected) or none, limit to one input per pipe
        return super.use(blockState, world, blockPos, playerEntity, hand, blockRayTraceResult);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
//        return new HoneyPipeTileEntity();
        return null;
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

    private boolean isTank(BlockPos facingPos, IWorld world, Direction direction) {
        if (world.getBlockState(facingPos).getBlock() instanceof HoneyPipe) return false;
        TileEntity entity = world.getBlockEntity(facingPos);
        return entity != null && entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).isPresent();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IWorld world = context.getLevel();
        BlockState stateIn = defaultBlockState();
        BlockPos currentPos = context.getClickedPos();
        return updateBlockState(world, stateIn, currentPos);
    }

    private BlockState updateBlockState(IWorld world, BlockState stateIn, BlockPos currentPos) {
        stateIn = updateState(stateIn, currentPos.above(), world, UP, Direction.DOWN);
        stateIn = updateState(stateIn, currentPos.below(), world, DOWN, Direction.UP);
        stateIn = updateState(stateIn, currentPos.east(), world, EAST, Direction.WEST);
        stateIn = updateState(stateIn, currentPos.west(), world, WEST, Direction.EAST);
        stateIn = updateState(stateIn, currentPos.south(), world, SOUTH, Direction.NORTH);
        stateIn = updateState(stateIn, currentPos.north(), world, NORTH, Direction.SOUTH);
        return stateIn;
    }

    private BlockState updateState(BlockState stateIn, BlockPos pos, IWorld world, EnumProperty<PipeState> stateProperty, Direction direction) {
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

    public enum PipeState implements IStringSerializable {
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
