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

    protected MixinBeeEntity(EntityType<? extends AnimalEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
    }

    @Shadow
    public BlockPos hivePos;

    @Shadow
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Inject(at = @At("HEAD"), method = "doesHiveHaveSpace(Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private void doesHiveHaveSpace(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        TileEntity tileentity = this.world.getTileEntity(pos);
        callback.setReturnValue((tileentity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) tileentity).isFullOfBees())
                || (tileentity instanceof ApiaryTileEntity && !((ApiaryTileEntity) tileentity).isFullOfBees())
                || (tileentity instanceof BeehiveTileEntity && !((BeehiveTileEntity) tileentity).isFullOfBees()));
    }

    @Inject(at = @At("HEAD"), method = "isHiveValid()Z", cancellable = true)
    public void isHiveValid(CallbackInfoReturnable<Boolean> callback) {
        if(this.hasHive()) {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                TileEntity blockEntity = this.world.getTileEntity(this.hivePos);
                callback.setReturnValue(blockEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof BeehiveTileEntity);
            }
        }
        callback.setReturnValue(false);
    }

/*    @Mixin(BeeEntity.UpdateBeehiveGoal.class)
    public static abstract class UpdateBeehiveGoal extends BeeEntity.PassiveGoal {

        public UpdateBeehiveGoal(){
            super();
        }

        @Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/passive/BeeEntity$UpdateBeehiveGoal;getNearbyFreeHives()Ljava/util/List;", cancellable = true)
        public List<BlockPos> getNearbyFreeHives(CallbackInfoReturnable<List<BlockPos>> cir) {
            BlockPos blockpos = BeeEntity.this.getBlockPos();
            PointOfInterestManager pointofinterestmanager = ((ServerWorld)BeeEntity.this.world).getPointOfInterestManager();
            Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b((p_226486_0_) -> {
                return p_226486_0_ == PointOfInterestType.BEEHIVE || p_226486_0_ == PointOfInterestType.BEE_NEST;
            }, blockpos, 20, PointOfInterestManager.Status.ANY);
            return stream.map(PointOfInterest::getPos).filter((p_226487_1_) -> {
                return BeeEntity.this.doesHiveHaveSpace(p_226487_1_);
            }).sorted(Comparator.comparingDouble((p_226488_1_) -> {
                return p_226488_1_.distanceSq(blockpos);
            })).collect(Collectors.toList());
        }
    }*/
}
