package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.registry.ModPOIs;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(BeeEntity.class)
public abstract class MixinBeeEntity extends AnimalEntity {

    protected MixinBeeEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow(aliases = "field_226369_bI_")
    public BlockPos hivePos;

    @Shadow(aliases = "func_226409_eA_()Z")
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Inject(at = @At("HEAD"), method = "doesHiveHaveSpace(Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private void doesHiveHaveSpace(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        TileEntity tileentity = this.world.getTileEntity(pos);
        if ((tileentity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) tileentity).isFullOfBees())
                || (tileentity instanceof ApiaryTileEntity && !((ApiaryTileEntity) tileentity).isFullOfBees())) {
            callback.setReturnValue(true);
        }
    }

    public boolean isInvulnerableTo(DamageSource p_180431_1_) {
        if (getActivePotionEffect(Effects.WATER_BREATHING) != null && p_180431_1_ == DamageSource.DROWN) {
            return true;
        }
        return super.isInvulnerableTo(p_180431_1_);
    }

    @Inject(at = @At("HEAD"), method = "isHiveValid()Z", cancellable = true)
    public void isHiveValid(CallbackInfoReturnable<Boolean> callback) {
        if (this.hasHive()) {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                TileEntity blockEntity = this.world.getTileEntity(this.hivePos);
                if ((blockEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) blockEntity).isAllowedBee())
                        || (blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).isAllowedBee())) {
                    callback.setReturnValue(true);
                }
            }
        }
    }

    @Mixin(BeeEntity.EnterBeehiveGoal.class)
    abstract static class MixinEnterBeehiveGoal {

        @Shadow(aliases = {"this$0"})
        private BeeEntity beeEntity;

        @Inject(at = @At("HEAD"), method = "canBeeStart()Z", cancellable = true)
        public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
            if (beeEntity.hasHive() && beeEntity.canEnterHive() && beeEntity.hivePos != null && beeEntity.hivePos.withinDistance(beeEntity.getPositionVec(), 2.0D)) {
                TileEntity tileentity = beeEntity.world.getTileEntity(beeEntity.hivePos);
                if (tileentity instanceof BeehiveTileEntity) {
                    BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) tileentity;
                    if (!beehivetileentity.isFullOfBees()) {
                        cir.setReturnValue(true);
                    } else {
                        beeEntity.hivePos = null;
                    }
                } else if (tileentity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity beehivetileentity = (ApiaryTileEntity) tileentity;
                    if (!beehivetileentity.isFullOfBees()) {
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
        public void startExecuting() {
            if (beeEntity.hivePos != null) {
                TileEntity tileentity = beeEntity.world.getTileEntity(beeEntity.hivePos);
                if (tileentity != null) {
                    if (tileentity instanceof BeehiveTileEntity) {
                        BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) tileentity;
                        beehivetileentity.tryEnterHive(beeEntity, beeEntity.hasNectar());
                    } else if (tileentity instanceof ApiaryTileEntity) {
                        ApiaryTileEntity beehivetileentity = (ApiaryTileEntity) tileentity;
                        beehivetileentity.tryEnterHive(beeEntity, beeEntity.hasNectar(), false);
                    }
                }
            }
        }
    }

    @Mixin(BeeEntity.UpdateBeehiveGoal.class)
    public static abstract class MixinUpdateBeehiveGoal {

        @Shadow(aliases = {"this$0"})
        private BeeEntity beeEntity;

        @Inject(at = @At(value = "HEAD"), method = "getNearbyFreeHives()Ljava/util/List;", cancellable = true)
        public void getNearbyFreeHives(CallbackInfoReturnable<List<BlockPos>> cir) {
            BlockPos blockpos = beeEntity.getBlockPos();
            PointOfInterestManager pointofinterestmanager = ((ServerWorld) beeEntity.world).getPointOfInterestManager();
            Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b(pointOfInterestType -> pointOfInterestType == PointOfInterestType.BEEHIVE
                    || pointOfInterestType == PointOfInterestType.BEE_NEST
                    || pointOfInterestType == ModPOIs.TIERED_BEEHIVE_POI.get(), blockpos, 20, PointOfInterestManager.Status.ANY);
            cir.setReturnValue(stream.map(PointOfInterest::getPos)
                    .filter(((BeeEntityAccessor) beeEntity)::callDoesHiveHaveSpace)
                    .sorted(Comparator.comparingDouble(blockPos -> blockPos.distanceSq(blockpos)))
                    .collect(Collectors.toList()));
        }
    }

    @Mixin(BeeEntity.FindBeehiveGoal.class)
    public static abstract class MixinFindBeehiveGoal {

        @Shadow(aliases = {"this$0"})
        private BeeEntity beeEntity;

        @Shadow
        public boolean isCloseEnough(BlockPos pos) {
            throw new IllegalStateException("Mixin failed to shadow isCloseEnough()");
        }

        @Inject(at = @At("HEAD"), method = "canBeeStart()Z", cancellable = true)
        public void canBeeStart(CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(beeEntity.hivePos != null && !beeEntity.detachHome() && beeEntity.canEnterHive() && !this.isCloseEnough(beeEntity.hivePos) && beeEntity.isHiveValid());
        }
    }
}
