package com.teamresourceful.resourcefulbees.common.entity.goals.base;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class WorkerGoal extends Goal {

    private long startTime = -1;

    @Override
    public final boolean canContinueToUse() {
        this.startTime = System.currentTimeMillis();
        return hasWork();
    }

    @Override
    public final void tick() {

        long time = 50 - (System.currentTimeMillis() - this.startTime);
        if (time < 10)
            time = 10;
        time += System.currentTimeMillis();

        while (System.currentTimeMillis() < time) {
            boolean again = doWork();

            if (!hasWork() || !again) return;
        }
    }

    protected abstract boolean hasWork();

    protected abstract boolean doWork();
}
