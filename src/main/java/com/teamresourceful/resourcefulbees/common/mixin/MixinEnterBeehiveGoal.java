package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.BeeEnterHiveGoal.class)
public abstract class MixinEnterBeehiveGoal {
    @Final
    @Mutable
    @Shadow(aliases = "field_226466_b_")
    private Bee this$0;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/animal/Bee;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.this$0 = beeEntity;
    }

    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        if (this$0.hasHive() && this$0.wantsToEnterHive() && this$0.getHivePos() != null && this$0.getHivePos().closerThan(this$0.blockPosition(), 2.0D)) {
            BlockEntity blockEntity = this$0.level.getBlockEntity(this$0.getHivePos());
            if (blockEntity instanceof BeehiveBlockEntity hive) {
                if (!hive.isFull()) {
                    cir.setReturnValue(true);
                } else {
                    ((BeeEntityAccessor) this$0).setHivePos(null);
                }
            } else if (blockEntity instanceof ApiaryBlockEntity apiary) {
                if (apiary.hasSpace()) {
                    cir.setReturnValue(true);
                } else {
                    ((BeeEntityAccessor) this$0).setHivePos(null);
                }
            }
        }
    }

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    public void onStart(CallbackInfo ci) {
        if (this$0.getHivePos() != null && this$0.level.getBlockEntity(this$0.getHivePos()) instanceof ApiaryBlockEntity apiary) {
            apiary.tryEnterHive(this$0, this$0.hasNectar(), 0);
            ci.cancel();
        }
    }
}