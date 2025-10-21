package com.teamresourceful.resourcefulbees.common.entities.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class BeePathNavigation extends FlyingPathNavigation {

    protected final BooleanSupplier canTick;

    public BeePathNavigation(Mob mob, Level level, BooleanSupplier canTick) {
        super(mob, level);
        setCanOpenDoors(false);
        setCanFloat(false);
        setCanPassDoors(true);
        this.canTick = canTick;
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos.below()).isAir();
    }

    @Override
    public void tick() {
        if (canTick.getAsBoolean()) {
            super.tick();
        }
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new BeeNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }
}
