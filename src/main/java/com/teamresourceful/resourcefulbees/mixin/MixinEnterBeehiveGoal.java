package com.teamresourceful.resourcefulbees.mixin;

import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeEntity.EnterBeehiveGoal.class)
public abstract class MixinEnterBeehiveGoal {
    @Unique
    private BeeEntity beeEntity;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/entity/passive/BeeEntity;)V", at = @At(value = "RETURN"))
    private void init(BeeEntity beeEntity, CallbackInfo ci) {
        this.beeEntity = beeEntity;
    }

    @Inject(at = @At("HEAD"), method = "canBeeUse()Z", cancellable = true)
    public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
        if (beeEntity.hasHive() && beeEntity.wantsToEnterHive() && beeEntity.getHivePos() != null && beeEntity.getHivePos().closerThan(beeEntity.position(), 2.0D)) {
            TileEntity blockEntity = beeEntity.level.getBlockEntity(beeEntity.getHivePos());
            if (blockEntity instanceof BeehiveTileEntity) {
                BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) blockEntity;
                if (!beehivetileentity.isFull()) {
                    cir.setReturnValue(true);
                } else {
                    ((BeeEntityAccessor)beeEntity).setHivePos(null);
                }
            } else if (blockEntity instanceof ApiaryTileEntity) {
                ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) blockEntity;
                if (apiaryTileEntity.hasSpace()) {
                    cir.setReturnValue(true);
                } else {
                    ((BeeEntityAccessor)beeEntity).setHivePos(null);
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
        if (beeEntity.getHivePos() != null) {
            TileEntity tileentity = beeEntity.level.getBlockEntity(beeEntity.getHivePos());
            if (tileentity != null) {
                if (tileentity instanceof BeehiveTileEntity) {
                    BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) tileentity;
                    beehivetileentity.addOccupant(beeEntity, beeEntity.hasNectar());
                } else if (tileentity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileentity;
                    apiaryTileEntity.tryEnterHive(beeEntity, beeEntity.hasNectar(), false);
                }
            }
        }
    }
}
