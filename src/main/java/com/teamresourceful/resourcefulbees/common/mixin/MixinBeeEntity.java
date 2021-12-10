package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.tileentity.TieredBeehiveTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.class)
public abstract class MixinBeeEntity extends Animal {

    protected MixinBeeEntity(EntityType<? extends Animal> entityType, Level world) {
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
        BlockEntity blockEntity = this.level.getBlockEntity(pos);
        if ((blockEntity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) blockEntity).isFull())
                || (blockEntity instanceof ApiaryTileEntity apiary && apiary.hasSpace())
                || (blockEntity instanceof BeehiveBlockEntity beeHive && !beeHive.isFull())) {
            callback.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "isHiveValid()Z", cancellable = true)
    public void isHiveValid(CallbackInfoReturnable<Boolean> callback) {
        if (this.hasHive()) {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                BlockEntity blockEntity = this.level.getBlockEntity(this.hivePos);
                if ((blockEntity instanceof TieredBeehiveTileEntity tieredHive && tieredHive.isAllowedBee())
                        || (blockEntity instanceof ApiaryTileEntity apiary && apiary.isAllowedBee())) {
                    callback.setReturnValue(true);
                }
            }
        }
    }
}
