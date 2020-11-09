package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import java.util.List;

public class BeeAngerGoal extends HurtByTargetGoal {
    private final ResourcefulBee bee;

    public BeeAngerGoal(ResourcefulBee beeIn) {
        super(beeIn);
        bee = beeIn;
    }

    public boolean shouldContinueExecuting() {
        return bee.getAngerTime() > 0 && super.shouldContinueExecuting();
    }

    protected void setAttackTarget(@Nonnull MobEntity mobIn, @Nonnull LivingEntity targetIn) {
        if (mobIn instanceof BeeEntity && this.goalOwner.canEntityBeSeen(targetIn)) {
            mobIn.setAttackTarget(targetIn);
        }
    }

    @Override
    protected void alertOthers() {
        double d0 = this.getTargetDistance();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.method_29968(this.goalOwner.getPositionVec()).grow(d0, 10.0D, d0);
        List<MobEntity> list = this.goalOwner.world.getEntitiesIncludingUngeneratedChunks(BeeEntity.class, axisalignedbb);
        for (MobEntity mobEntity : list) {
            if (this.goalOwner.getRevengeTarget() != null)
                this.setAttackTarget(mobEntity, this.goalOwner.getRevengeTarget());
        }
    }
}
