package com.resourcefulbees.resourcefulbees.effects;

import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import org.jetbrains.annotations.NotNull;

public class Calming extends Effect {
    public Calming(EffectType beneficial, int color) {
        super(beneficial, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int level) {
        if (entity instanceof IAngerable) {
            IAngerable iAngerable = (IAngerable) entity;
            iAngerable.stopBeingAngry();
        }
        super.applyEffectTick(entity, level);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int level) {
        return duration % 5 == 0;
    }
}
