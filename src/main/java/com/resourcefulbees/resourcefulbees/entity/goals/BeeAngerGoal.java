package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeeAngerGoal extends HurtByTargetGoal {
    private final ResourcefulBee bee;

    public BeeAngerGoal(ResourcefulBee beeIn) {
        super(beeIn);
        bee = beeIn;
    }

    @Override
    public boolean canContinueToUse() {
        return bee.getRemainingPersistentAngerTime() > 0 && super.canContinueToUse();
    }

    @Override
    protected void alertOther(@NotNull MobEntity mobIn, @NotNull LivingEntity targetIn) {
        if (mobIn instanceof BeeEntity && this.mob.canSee(targetIn)) {
            mobIn.setTarget(targetIn);
        }
    }

    @Override
    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
        List<BeeEntity> list = this.mob.level.getLoadedEntitiesOfClass(BeeEntity.class, axisalignedbb);
        list.forEach(beeEntity -> {
            if (this.mob.getLastHurtByMob() != null)
                this.alertOther(beeEntity, this.mob.getLastHurtByMob());
        });
    }
}
