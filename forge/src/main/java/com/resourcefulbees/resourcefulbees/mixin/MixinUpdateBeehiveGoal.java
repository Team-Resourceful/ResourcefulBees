package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.registry.ModPOIs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(Bee.BeeLocateHiveGoal.class)
public abstract class MixinUpdateBeehiveGoal {

    @Unique
    private Bee beeEntity;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/world/entity/animal/Bee;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.beeEntity = beeEntity;
    }

    @Inject(at = @At(value = "HEAD"), method = "findNearbyHivesWithSpace()Ljava/util/List;", cancellable = true)
    public void findNearbyHivesWithSpace(CallbackInfoReturnable<List<BlockPos>> cir) {
        BlockPos blockpos = beeEntity.blockPosition();
        PoiManager pointofinterestmanager = ((ServerLevel) beeEntity.level).getPoiManager();
        Stream<PoiRecord> stream = pointofinterestmanager.getInRange(pointOfInterestType -> pointOfInterestType == PoiType.BEEHIVE
                || pointOfInterestType == PoiType.BEE_NEST
                || pointOfInterestType == ModPOIs.TIERED_BEEHIVE_POI.get(), blockpos, 20, PoiManager.Occupancy.ANY);
        cir.setReturnValue(stream.map(PoiRecord::getPos)
                .filter(((BeeEntityAccessor) beeEntity)::callDoesHiveHaveSpace)
                .sorted(Comparator.comparingDouble(blockPos -> blockPos.distSqr(blockpos)))
                .collect(Collectors.toList()));
    }
}
