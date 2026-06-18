package com.teamresourceful.resourcefulbees.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class HoneyGlass extends TransparentBlock {

    private final boolean collidePlayer; //if true player cannot go through block but bee can - if false player can go through block but bee cannot

    public HoneyGlass(BlockBehaviour.Properties properties, boolean collidePlayer) {
        super(properties);
        this.collidePlayer = collidePlayer;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            if ((collidePlayer && isBeeContext(entityContext)) || (isPlayerContext(entityContext) && !collidePlayer)) return Shapes.empty();
            else return state.getShape(level, pos);
        }
        return Shapes.empty();
    }

    private static boolean isPlayerContext(EntityCollisionContext context) {
        return context.getEntity() instanceof Player;
    }

    private static boolean isBeeContext(EntityCollisionContext context) {
        return context.getEntity() instanceof Bee;
    }

    @Override
    public @Nullable PathType getBlockPathType(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @Nullable Mob mob) {
        return collidePlayer && mob instanceof Bee ? PathType.OPEN : PathType.BLOCKED;
    }

}
