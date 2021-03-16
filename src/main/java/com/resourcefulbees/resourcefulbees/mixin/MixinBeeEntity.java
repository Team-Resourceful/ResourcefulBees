package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeEntity.class)
public abstract class MixinBeeEntity extends AnimalEntity {

    protected MixinBeeEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public BlockPos hivePos;

    @Shadow
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Inject(at = @At("HEAD"), method = "doesHiveHaveSpace(Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private void doesHiveHaveSpace(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        TileEntity tileentity = this.level.getBlockEntity(pos);
        if ((tileentity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) tileentity).isFull())
                || (tileentity instanceof ApiaryTileEntity && !((ApiaryTileEntity) tileentity).isFullOfBees())
                || (tileentity instanceof BeehiveTileEntity && !((BeehiveTileEntity) tileentity).isFull())) {
            callback.setReturnValue(true);
        }
    }

    //Leaving this here for now - oreo
    /*@Inject(at = @At("HEAD"), method = "getStandingEyeHeight(Lnet/minecraft/entity/Pose;Lnet/minecraft/entity/EntitySize;)F", cancellable = true)
    public void getStandingEyeHeight(Pose pose, EntitySize size, CallbackInfoReturnable<Float> callback) {
        callback.setReturnValue(this.isBaby() ? size.height * 0.25F : size.height * 0.5F);
    }*/

    @Inject(at = @At("HEAD"), method = "isHiveValid()Z", cancellable = true)
    public void isHiveValid(CallbackInfoReturnable<Boolean> callback) {
        if (this.hasHive()) {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                TileEntity blockEntity = this.level.getBlockEntity(this.hivePos);
                if ((blockEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) blockEntity).isAllowedBee())
                        || (blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).isAllowedBee())) {
                    callback.setReturnValue(true);
                }
            }
        }
    }
}
