package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeEntityInvoker;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModPOIs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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

    @Final
    @Mutable
    @Shadow(aliases = "field_226485_b_")
    private Bee this$0;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<init>(Lnet/minecraft/entity/passive/BeeEntity;)V", at = @At(value = "RETURN"))
    private void init(Bee beeEntity, CallbackInfo ci) {
        this.this$0 = beeEntity;
    }

    @Inject(at = @At(value = "HEAD"), method = "findNearbyHivesWithSpace()Ljava/util/List;", cancellable = true)
    public void findNearbyHivesWithSpace(CallbackInfoReturnable<List<BlockPos>> cir) {
        BlockPos blockpos = this$0.blockPosition();
        PoiManager pointofinterestmanager = ((ServerLevel) this$0.level).getPoiManager();
        Stream<PoiRecord> stream = pointofinterestmanager.getInRange(pointOfInterestType -> pointOfInterestType == PoiType.BEEHIVE
                || pointOfInterestType == PoiType.BEE_NEST
                || pointOfInterestType == ModPOIs.TIERED_BEEHIVE_POI.get(), blockpos, 20, PoiManager.Occupancy.ANY);
        cir.setReturnValue(stream.map(PoiRecord::getPos)
                .filter(((BeeEntityInvoker) this$0)::callDoesHiveHaveSpace)
                .sorted(Comparator.comparingDouble(blockPos -> blockPos.distSqr(blockpos)))
                .collect(Collectors.toList()));
    }
}
