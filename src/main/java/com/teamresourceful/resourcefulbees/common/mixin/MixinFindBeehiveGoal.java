package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeEntityInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.BeeGoToHiveGoal.class)
public abstract class MixinFindBeehiveGoal {

    @Final
    @Mutable
    @Shadow(aliases = "field_226467_b_")
    private Bee this$0;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/entity/passive/BeeEntity;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.this$0 = beeEntity;
    }

    @Shadow
    public boolean hasReachedTarget(BlockPos pos) {
        throw new IllegalStateException("Mixin failed to shadow isCloseEnough()");
    }

    //This mixin is needed due to the Apiary not being in the beehives tag
    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        BeeEntityInvoker beeEntityInvoker = (BeeEntityInvoker) this$0;

        cir.setReturnValue(this$0.getHivePos() != null && !this$0.hasRestriction() && beeEntityInvoker.callWantsToEnterHive() && !this.hasReachedTarget(this$0.getHivePos()) && this$0.isHiveValid());
    }
}
