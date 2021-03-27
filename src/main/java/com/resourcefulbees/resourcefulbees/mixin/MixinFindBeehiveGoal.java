package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeEntity.FindBeehiveGoal.class)
public abstract class MixinFindBeehiveGoal {

    @Unique
    private BeeEntity beeEntity;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/entity/passive/BeeEntity;)V", at = @At(value = "RETURN"))
    private void init(BeeEntity beeEntity, CallbackInfo ci) {
        this.beeEntity = beeEntity;
    }

    @Shadow
    public boolean hasReachedTarget(BlockPos pos) {
        throw new IllegalStateException("Mixin failed to shadow isCloseEnough()");
    }

    //This mixin is needed due to the Apiary not being in the beehives tag
    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(beeEntity.hivePos != null && !beeEntity.hasRestriction() && beeEntity.wantsToEnterHive() && !this.hasReachedTarget(beeEntity.hivePos) && beeEntity.isHiveValid());
    }  //return BeeEntity.this.hivePos != null && !BeeEntity.this.detachHome() && BeeEntity.this.canEnterHive() && !this.isCloseEnough(BeeEntity.this.hivePos) && BeeEntity.this.world.getBlockState(BeeEntity.this.hivePos).isIn(BlockTags.BEEHIVES);
}
