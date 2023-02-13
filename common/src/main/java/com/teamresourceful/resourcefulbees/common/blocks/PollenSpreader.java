package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.datafixers.kinds.IdF;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class PollenSpreader extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public PollenSpreader(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    private void updateChildren(BlockState spreader, BlockPos spreaderPos, Level level) {
        detectChildren(spreader, spreaderPos, level);
//        BlockEntity entity = level.getBlockEntity(spreaderPos);
////        if (entity instanceof PollenSpreaderEntity spreaderEntity) {
//        Direction direction = spreader.getValue(FACING).getOpposite();
//        Direction left = direction.getClockWise();
//        Direction right = direction.getCounterClockWise();
//        BlockPos corner1 = spreaderPos.relative(direction, 1).relative(left, 2);
//        BlockPos corner2 = spreaderPos.relative(direction, 5).relative(right, 2);
//        AABB box = new AABB(corner1, corner2);
//        List<Block> states = level.getBlockStates(box).map(BlockStateBase::getBlock).toList();
//        if (states.size() == 1) System.out.println("shape correct");
//        else System.out.println("shape incorrect");
//        }
    }

    private void detectChildren(BlockState spreader, BlockPos spreaderPos, Level level) {
        Direction direction = spreader.getValue(FACING).getOpposite();
        int size = 0;
        BlockPos pos = spreaderPos.relative(direction, 1);
        // get length
        boolean found;
        do {
            found = level.getBlockState(pos.relative(direction, size)).getBlock() instanceof PollenSpreader.Fan;
            if (!found) break;
            size++;
        } while (size < 5);
        while (size > 0) {
            for (int i = 1 - size; i <= 0; i++) {
                BlockPos corner1 = pos.relative(direction.getClockWise(), i);
                BlockPos corner2 = corner1.relative(direction, size - 1).relative(direction.getClockWise(), size - 1);

                AABB box = new AABB(corner1, corner2);
                Stream<BlockState> states = level.getBlockStates(box);
                if (states.allMatch(b -> b.getBlock() instanceof PollenSpreader.Fan)) {
                    System.out.printf("Match found [%d, %d] - [%d, %d], %dx%d%n", (int) box.minX, (int) box.minZ, (int) box.maxX, (int) box.maxZ, (int) box.getXsize() + 1, (int) box.getZsize() + 1);
                    return;
                }
            }
            size--;
        }
        System.out.println("no shapes possible");
    }

    public static class Fan extends Block {

        public Fan(Properties properties) {
            super(properties);
        }

        public BlockPos getSpreader(Level level, BlockPos pos) {
            List<BlockPos> blocks = new LinkedList<>();
            for (int i = -6; i <= 6; i++) {
                for (int j = -6; j <= 6; j++) {
                    blocks.add(new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + j));
                }
            }
            blocks = blocks.stream().filter(p -> level.getBlockState(p).getBlock() instanceof PollenSpreader).toList();
            if (blocks.isEmpty()) return null;
            else return blocks.get(0);
        }

        private void updateSpreader(Level level, BlockPos pos) {
            BlockPos spreaderPos = getSpreader(level, pos);
            if (spreaderPos == null) return;
            BlockState state = level.getBlockState(spreaderPos);
            if (state.getBlock() instanceof PollenSpreader spreader) {
                spreader.updateChildren(state, spreaderPos, level);
            }
        }

        @Override
        public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
            super.onPlace(state, level, pos, oldState, isMoving);
            updateSpreader(level, pos);
        }

        @Override
        public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
            super.onRemove(state, level, pos, newState, isMoving);
            updateSpreader(level, pos);
        }

    }
}
