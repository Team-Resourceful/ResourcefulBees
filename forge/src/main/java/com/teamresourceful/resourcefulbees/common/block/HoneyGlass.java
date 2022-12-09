package com.teamresourceful.resourcefulbees.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyGlass extends GlassBlock {

    private final boolean collidePlayer; //if true player cannot go through block but bee can - if false player can go through block but bee cannot

    public HoneyGlass(Properties properties, boolean collidePlayer) {
        super(properties);
        this.collidePlayer = collidePlayer;
    }

    @Override
    @SuppressWarnings("deprecation")
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
    public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return collidePlayer && mob instanceof Bee ? BlockPathTypes.OPEN : BlockPathTypes.BLOCKED;
    }


}
