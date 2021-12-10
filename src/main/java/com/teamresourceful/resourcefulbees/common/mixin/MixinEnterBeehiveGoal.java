package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
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

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/entity/passive/BeeEntity;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.this$0 = beeEntity;
    }

    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        if (this$0.hasHive() && this$0.wantsToEnterHive() && this$0.getHivePos() != null && this$0.getHivePos().closerThan(this$0.position(), 2.0D)) {
            BlockEntity blockEntity = this$0.level.getBlockEntity(this$0.getHivePos());
            if (blockEntity instanceof BeehiveBlockEntity hive) {
                if (!hive.isFull()) {
                    cir.setReturnValue(true);
                } else {
                    ((BeeEntityAccessor) this$0).setHivePos(null);
                }
            } else if (blockEntity instanceof ApiaryTileEntity apiary) {
                if (apiary.hasSpace()) {
                    cir.setReturnValue(true);
                } else {
                    ((BeeEntityAccessor) this$0).setHivePos(null);
                }
            }
        }
    }

    //TODO convert this to an inject
    /**
     * @author epic_oreo
     * @reason crashes when switching to vanilla code due to hivePos being null. retained vanilla checks in overwrite.
     */
    @Overwrite()
    public void start() {
        if (this$0.getHivePos() != null) {
            BlockEntity tileentity = this$0.level.getBlockEntity(this$0.getHivePos());
            if (tileentity != null) {
                if (tileentity instanceof BeehiveBlockEntity hive) {
                    hive.addOccupant(this$0, this$0.hasNectar());
                } else if (tileentity instanceof ApiaryTileEntity apiary) {
                    apiary.tryEnterHive(this$0, this$0.hasNectar(), false);
                }
            }
        }
    }
}