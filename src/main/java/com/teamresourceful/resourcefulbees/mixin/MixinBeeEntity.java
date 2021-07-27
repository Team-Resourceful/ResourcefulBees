package com.teamresourceful.resourcefulbees.mixin;

import com.teamresourceful.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
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
    private BlockPos hivePos;

    @Shadow
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Inject(at = @At("HEAD"), method = "doesHiveHaveSpace", cancellable = true)
    private void doesHiveHaveSpace(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        TileEntity blockEntity = this.level.getBlockEntity(pos);
        if ((blockEntity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) blockEntity).isFull())
                || (blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).hasSpace())
                || (blockEntity instanceof BeehiveTileEntity && !((BeehiveTileEntity) blockEntity).isFull())) {
            callback.setReturnValue(true);
        }
    }

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
