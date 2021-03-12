package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeEntity.FindBeehiveGoal.class)
public abstract class MixinFindBeehiveGoal {

    @Dynamic
    @Final
    @Shadow(aliases = {"BeeEntity$EnterBeehiveGoal.this$0", "this$0", "..."})
    private BeeEntity this$0;

    @Shadow
    public boolean hasReachedTarget(BlockPos pos) {
        throw new IllegalStateException("Mixin failed to shadow isCloseEnough()");
    }

    //This mixin is needed due to the Apiary not being in the beehives tag
    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this$0.hivePos != null && !this$0.hasRestriction() && this$0.wantsToEnterHive() && !this.hasReachedTarget(this$0.hivePos) && this$0.isHiveValid());
    }  //return BeeEntity.this.hivePos != null && !BeeEntity.this.detachHome() && BeeEntity.this.canEnterHive() && !this.isCloseEnough(BeeEntity.this.hivePos) && BeeEntity.this.world.getBlockState(BeeEntity.this.hivePos).isIn(BlockTags.BEEHIVES);
}
