package com.teamresourceful.resourcefulbees.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import org.jetbrains.annotations.NotNull;

public class Calming extends MobEffect {
    public Calming(MobEffectCategory beneficial, int color) {
        super(beneficial, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int level) {
        if (entity instanceof NeutralMob) {
            NeutralMob iAngerable = (NeutralMob) entity;
            iAngerable.stopBeingAngry();
        }
        super.applyEffectTick(entity, level);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int level) {
        return duration % 5 == 0;
    }
}
