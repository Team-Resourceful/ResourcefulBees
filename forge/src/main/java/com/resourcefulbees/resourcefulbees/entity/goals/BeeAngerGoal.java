package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
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
    protected void alertOther(@NotNull Mob mobIn, @NotNull LivingEntity targetIn) {
        if (mobIn instanceof Bee && this.mob.canSee(targetIn)) {
            mobIn.setTarget(targetIn);
        }
    }

    @Override
    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AABB axisalignedbb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
        List<Bee> list = this.mob.level.getLoadedEntitiesOfClass(Bee.class, axisalignedbb);
        list.forEach(beeEntity -> {
            if (this.mob.getLastHurtByMob() != null)
                this.alertOther(beeEntity, this.mob.getLastHurtByMob());
        });
    }
}
