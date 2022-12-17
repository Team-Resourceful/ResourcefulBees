package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.blockentity.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Bee.BeeEnterHiveGoal.class)
public abstract class MixinBeeEnterHiveGoal {
    @Final
    @Mutable
    @Shadow(aliases = {"field_20367", "f_27970_"})
    private Bee this$0;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/animal/Bee;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.this$0 = beeEntity;
    }

    @Inject(
        method = "canBeeUse",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
        ),
        cancellable = true
    )
    public void onCanBeeUse(CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantConditions
        BlockEntity blockEntity = this$0.level.getBlockEntity(this$0.getHivePos());
        if (blockEntity instanceof BeeHolderBlockEntity holder) {
            if (holder.hasSpace()) {
                cir.setReturnValue(true);
            } else {
                ((BeeEntityAccessor) this$0).setHivePos(null);
            }
        }
    }

    @Inject(method = "start", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onStart(CallbackInfo ci, BlockEntity blockentity) {
        if (blockentity instanceof BeeHolderBlockEntity holder) {
            holder.tryEnterHive(this$0, this$0.hasNectar(), 0);
        }
    }
}