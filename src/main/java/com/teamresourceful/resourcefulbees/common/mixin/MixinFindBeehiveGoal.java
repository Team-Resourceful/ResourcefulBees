package com.teamresourceful.resourcefulbees.common.mixin;

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
        BeeEntityInvoker beeEntityInvoker = (BeeEntityInvoker) beeEntity;

        cir.setReturnValue(beeEntity.getHivePos() != null && !beeEntity.hasRestriction() && beeEntityInvoker.callWantsToEnterHive() && !this.hasReachedTarget(beeEntity.getHivePos()) && beeEntity.isHiveValid());
    }
}
