package com.resourcefulbees.resourcefulbees.api.traitdata;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class BeeAura {

    public AuraType auraType;
    public int strength = 0;
    public String damageType = null;
    public boolean calmingDisabled = false;
    private Effect potionEffect = Effects.MOVEMENT_SPEED;

    public BeeAura() {
        // intentionally left empty will allow for default values to be saved
    }

    public int getDamage() {
        return Math.max(Math.min(strength, 20), 0);
    }

    public int getHealing() {
        return Math.max(Math.min(strength, 20), 0);
    }

    public int getExperience() {
        return Math.max(Math.min(strength, 20), 0);
    }

    public EffectInstance getInstance(int duration) {
        return new EffectInstance(potionEffect, duration, Math.max(Math.min(strength, 4), 0));
    }

    public BeeAura(AuraType type, Effect effect, String damageType, int strength, boolean calmingDisabled) {
        this.auraType = type;
        this.potionEffect = effect;
        this.strength = strength;
        this.damageType = damageType;
        this.calmingDisabled = calmingDisabled;
    }

    public enum AuraType {
        BURNING,
        POTION,
        DAMAGING,
        HEALING,
        EXPERIENCE;
    }
}
