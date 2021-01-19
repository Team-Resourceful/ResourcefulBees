package com.resourcefulbees.resourcefulbees.effects;

import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Calming extends Effect {
    protected Calming(EffectType beneficial, int color) {
        super(beneficial, color);
    }

    @Override
    public void performEffect(LivingEntity entity, int level) {
        if (entity instanceof IAngerable) {
            IAngerable angerable = (IAngerable) entity;
            angerable.stopAnger();
        }
        super.performEffect(entity, level);
    }

    @Override
    public boolean isReady(int duration, int level) {
        if (duration % 5 == 0) return true;
        else return false;
    }
}
