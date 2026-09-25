package com.teamresourceful.resourcefulbees.mixin.common;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.common.entities.ai.behavior.HarvestTieredBeehive;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModVillagerProfessions;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {

    @Inject(method = "getWorkPackage", at = @At("RETURN"), cancellable = true)
    private static void resourcefulbees$addBeekeeperHarvest(
        Holder<VillagerProfession> profession,
        float speedModifier,
        CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir
    ) {
        if (!profession.is(ModVillagerProfessions.BEEKEEPER.holder())) {
            return;
        }

        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>> builder = ImmutableList.builder();
        builder.addAll(cir.getReturnValue());
        builder.add(Pair.of(0, new HarvestTieredBeehive(speedModifier)));
        cir.setReturnValue(builder.build());
    }
}