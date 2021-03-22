package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.registry.ModPOIs;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(BeeEntity.UpdateBeehiveGoal.class)
public abstract class MixinUpdateBeehiveGoal {

    @Unique
    private BeeEntity beeEntity;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/entity/passive/BeeEntity;)V", at = @At(value = "RETURN"))
    private void init(BeeEntity beeEntity, CallbackInfo ci) {
        this.beeEntity = beeEntity;
    }

    @Inject(at = @At(value = "HEAD"), method = "findNearbyHivesWithSpace()Ljava/util/List;", cancellable = true)
    public void findNearbyHivesWithSpace(CallbackInfoReturnable<List<BlockPos>> cir) {
        BlockPos blockpos = beeEntity.blockPosition();
        PointOfInterestManager pointofinterestmanager = ((ServerWorld) beeEntity.level).getPoiManager();
        Stream<PointOfInterest> stream = pointofinterestmanager.getInRange(pointOfInterestType -> pointOfInterestType == PointOfInterestType.BEEHIVE
                || pointOfInterestType == PointOfInterestType.BEE_NEST
                || pointOfInterestType == ModPOIs.TIERED_BEEHIVE_POI.get(), blockpos, 20, PointOfInterestManager.Status.ANY);
        cir.setReturnValue(stream.map(PointOfInterest::getPos)
                .filter(((BeeEntityAccessor) beeEntity)::callDoesHiveHaveSpace)
                .sorted(Comparator.comparingDouble(blockPos -> blockPos.distSqr(blockpos)))
                .collect(Collectors.toList()));
    }
}
