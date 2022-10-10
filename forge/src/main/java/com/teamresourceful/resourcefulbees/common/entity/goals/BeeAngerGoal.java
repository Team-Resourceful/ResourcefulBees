package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.NotNull;

public class BeeAngerGoal extends HurtByTargetGoal {

    private final ResourcefulBee bee;

    public BeeAngerGoal(ResourcefulBee bee) {
        super(bee);
        this.bee = bee;
    }

    @Override
    public boolean canContinueToUse() {
        return bee.getRemainingPersistentAngerTime() > 0 && super.canContinueToUse();
    }

    @Override
    protected void alertOther(@NotNull Mob mobIn, @NotNull LivingEntity targetIn) {
        if (mobIn instanceof Bee && this.mob.hasLineOfSight(targetIn)) {
            mobIn.setTarget(targetIn);
        }
    }

    @Override
    protected void alertOthers() {
        if (this.mob.getLastHurtByMob() != null) {
            double dist = this.getFollowDistance();
            for (Bee bee : this.mob.level.getEntitiesOfClass(Bee.class, this.mob.getBoundingBox().inflate(dist, 10.0D, dist))) {
                this.alertOther(bee, this.mob.getLastHurtByMob());
            }
        }
    }
}
