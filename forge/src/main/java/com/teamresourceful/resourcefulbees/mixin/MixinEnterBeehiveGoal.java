package com.teamresourceful.resourcefulbees.mixin;

import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
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
            BlockEntity blockEntity = beeEntity.level.getBlockEntity(beeEntity.hivePos);
            if (blockEntity instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity beehivetileentity = (BeehiveBlockEntity) blockEntity;
                if (!beehivetileentity.isFull()) {
                    cir.setReturnValue(true);
                } else {
                    beeEntity.hivePos = null;
                }
            } else if (blockEntity instanceof ApiaryTileEntity) {
                ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) blockEntity;
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
