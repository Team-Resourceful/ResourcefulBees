package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.registry.ModPOIs;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(BeeEntity.UpdateBeehiveGoal.class)
public abstract class MixinUpdateBeehiveGoal {

    @Dynamic
    @Final
    @Shadow(aliases = {"BeeEntity$EnterBeehiveGoal.this$0", "this$0", "..."})
    private BeeEntity this$0;

    @Inject(at = @At(value = "HEAD"), method = "findNearbyHivesWithSpace()Ljava/util/List;", cancellable = true)
    public void findNearbyHivesWithSpace(CallbackInfoReturnable<List<BlockPos>> cir) {
        BlockPos blockpos = this$0.blockPosition();
        PointOfInterestManager pointofinterestmanager = ((ServerWorld) this$0.level).getPoiManager();
        Stream<PointOfInterest> stream = pointofinterestmanager.getInRange(pointOfInterestType -> pointOfInterestType == PointOfInterestType.BEEHIVE
                || pointOfInterestType == PointOfInterestType.BEE_NEST
                || pointOfInterestType == ModPOIs.TIERED_BEEHIVE_POI.get(), blockpos, 20, PointOfInterestManager.Status.ANY);
        cir.setReturnValue(stream.map(PointOfInterest::getPos)
                .filter(((BeeEntityAccessor) this$0)::callDoesHiveHaveSpace)
                .sorted(Comparator.comparingDouble(blockPos -> blockPos.distSqr(blockpos)))
                .collect(Collectors.toList()));
    }
}
