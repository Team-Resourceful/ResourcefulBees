package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
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
    @Unique
    private Bee beeEntity;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/world/entity/animal/Bee;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.beeEntity = beeEntity;
    }

    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        if (beeEntity.hasHive() && beeEntity.wantsToEnterHive() && beeEntity.hivePos != null && beeEntity.hivePos.closerThan(beeEntity.position(), 2.0D)) {
            BlockEntity tileentity = beeEntity.level.getBlockEntity(beeEntity.hivePos);
            if (tileentity instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity beehivetileentity = (BeehiveBlockEntity) tileentity;
                if (!beehivetileentity.isFull()) {
                    cir.setReturnValue(true);
                } else {
                    beeEntity.hivePos = null;
                }
            } else if (tileentity instanceof ApiaryTileEntity) {
                ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileentity;
                if (apiaryTileEntity.hasSpace()) {
                    cir.setReturnValue(true);
                } else {
                    beeEntity.hivePos = null;
                }
            }
        }
    }

    /**
     * @author epic_oreo
     * @reason crashes when switching to vanilla code due to hivePos being null. retained vanilla checks in overwrite.
     */
    @Overwrite()
    public void start() {
        if (beeEntity.hivePos != null) {
            BlockEntity tileentity = beeEntity.level.getBlockEntity(beeEntity.hivePos);
            if (tileentity != null) {
                if (tileentity instanceof BeehiveBlockEntity) {
                    BeehiveBlockEntity beehivetileentity = (BeehiveBlockEntity) tileentity;
                    beehivetileentity.addOccupant(beeEntity, beeEntity.hasNectar());
                } else if (tileentity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileentity;
                    apiaryTileEntity.tryEnterHive(beeEntity, beeEntity.hasNectar(), false);
                }
            }
        }
    }
}
