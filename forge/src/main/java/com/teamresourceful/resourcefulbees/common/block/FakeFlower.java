package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.block.base.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.FakeFlowerEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.centrifuge.CentrifugeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FakeFlower extends RenderingBaseEntityBlock {

    private static final VoxelShape FULL_SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 2, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public FakeFlower(Properties arg) {
        super(arg);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return FULL_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FakeFlowerEntity(pos, state);
    }

}
