package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.platform.client.events.ModelBakingCompletedEvent;
import com.teamresourceful.resourcefulbees.platform.client.events.ModelModifyResultEvent;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @Inject(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void rbees$onApply(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller, CallbackInfo ci) {
        ModelBakingCompletedEvent.EVENT.fire(new ModelBakingCompletedEvent(reloadState.modelBakery()));
    }

    @Inject(
            method = "loadModels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            )
    )
    private void rbees$loadModels(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        ModelModifyResultEvent.EVENT.fire(new ModelModifyResultEvent(modelBakery.getBakedTopLevelModels(), modelBakery));
    }
}
