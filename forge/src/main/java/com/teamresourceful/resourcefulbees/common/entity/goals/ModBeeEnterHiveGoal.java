package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.common.blockentity.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModBeeEnterHiveGoal extends Goal {

    private final ResourcefulBee bee;

    public ModBeeEnterHiveGoal(ResourcefulBee bee) {
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        if (this.bee.isAngry()) {
            return false;
        }

        BlockPos pos = this.bee.getHivePos();
        if (pos != null && this.bee.wantsToEnterHive() && pos.closerThan(this.bee.blockPosition(), 2.0D)) {
            BlockEntity block = this.bee.level.getBlockEntity(pos);
            if (block instanceof BeehiveBlockEntity hive) {
                if (!hive.isFull()) {
                    return true;
                }
                ((BeeEntityAccessor) this.bee).setHivePos(null);
            } else if (block instanceof BeeHolderBlockEntity apiary) {
                if (apiary.hasSpace()) {
                    return true;
                }

                ((BeeEntityAccessor) this.bee).setHivePos(null);
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        BlockPos pos = this.bee.getHivePos();
        if (pos != null) {
            BlockEntity block = this.bee.level.getBlockEntity(pos);
            if (block instanceof BeehiveBlockEntity hive) {
                hive.addOccupant(this.bee, this.bee.hasNectar());
            } else if (block instanceof BeeHolderBlockEntity holder) {
                holder.tryEnterHive(this.bee, this.bee.hasNectar(), 0);
            }
        }
    }
}