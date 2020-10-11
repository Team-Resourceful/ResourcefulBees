package com.resourcefulbees.resourcefulbees.data;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BeeTrait {
    private final List<Pair<Effect, Integer>> potionDamageEffects;
    private final List<DamageSource> damageImmunities;
    private final List<Effect> potionImmunities;
    private final List<Pair<String, Integer>> damageTypes;
    private final List<String> specialAbilities;
    private final BasicParticleType particleEffect;
    private final boolean shouldSting;

    private BeeTrait(List<Pair<Effect, Integer>> potionDamageEffects, List<DamageSource> damageImmunities, List<Effect> potionImmunities, List<Pair<String, Integer>> damageTypes, List<String> specialAbilities, BasicParticleType particleEffect, boolean shouldSting) {
        this.potionDamageEffects = potionDamageEffects;
        this.damageImmunities = damageImmunities;
        this.potionImmunities = potionImmunities;
        this.damageTypes = damageTypes;
        this.specialAbilities = specialAbilities;
        this.particleEffect = particleEffect;
        this.shouldSting = shouldSting;
    }

    public boolean hasDamagePotionEffects(){ return this.potionDamageEffects != null && !this.potionDamageEffects.isEmpty(); }
    public boolean hasDamageImmunities(){ return this.damageImmunities != null && !this.damageImmunities.isEmpty(); }
    public boolean hasPotionImmunities(){ return this.potionImmunities != null && !this.potionImmunities.isEmpty(); }
    public boolean hasDamageTypes(){ return this.damageTypes != null && !this.damageTypes.isEmpty(); }
    public boolean hasSpecialAbilities(){ return this.specialAbilities != null && !this.specialAbilities.isEmpty(); }
    public boolean hasParticleEffect(){ return this.particleEffect != null; }
    public boolean shouldNotString() { return this.shouldSting; }

    public List<Pair<Effect, Integer>> getPotionDamageEffects(){
        return this.potionDamageEffects;
    }
    public List<DamageSource> getDamageImmunities(){
        return this.damageImmunities;
    }
    public List<Effect> getPotionImmunities(){
        return this.potionImmunities;
    }
    public List<Pair<String, Integer>> getDamageTypes(){
        return this.damageTypes;
    }
    public List<String> getSpecialAbilities(){
        return this.specialAbilities;
    }
    public BasicParticleType getParticleEffect(){
        return this.particleEffect;
    }

    public static class Builder {
        List<Pair<Effect, Integer>> potionDamageEffects = new ArrayList<>();
        List<DamageSource> damageImmunities = new ArrayList<>();
        List<Effect> potionImmunities = new ArrayList<>();
        List<Pair<String, Integer>> damageTypes = new ArrayList<>();
        List<String> specialAbilities = new ArrayList<>();
        BasicParticleType particleEffect;
        boolean shouldSting;

        public Builder(){}

        public Builder addDamagePotionEffects(List<Pair<Effect, Integer>> potionDamageEffects) {
            this.potionDamageEffects.addAll(potionDamageEffects);
            return this;
        }
        public Builder addDamagePotionEffect(Pair<Effect, Integer> potionDamageEffect) {
            this.potionDamageEffects.add(potionDamageEffect);
            return this;
        }
        public Builder addDamageImmunities(List<DamageSource> damageImmunities) {
            this.damageImmunities.addAll(damageImmunities);
            return this;
        }
        public Builder addDamageImmunity(DamageSource damageImmunity) {
            this.damageImmunities.add(damageImmunity);
            return this;
        }
        public Builder addPotionImmunities(List<Effect> potionImmunities) {
            this.potionImmunities.addAll(potionImmunities);
            return this;
        }
        public Builder addPotionImmunity(Effect potionImmunity) {
            this.potionImmunities.add(potionImmunity);
            return this;
        }
        public Builder addDamageTypes(List<Pair<String, Integer>> damageTypes) {
            this.damageTypes.addAll(damageTypes);
            return this;
        }
        public Builder addDamageType(Pair<String, Integer> damageType) {
            this.damageTypes.add(damageType);
            return this;
        }
        public Builder addSpecialAbilities(List<String> specialAbilities) {
            this.specialAbilities.addAll(specialAbilities);
            return this;
        }
        public Builder addSpecialAbility(String specialAbility) {
            this.specialAbilities.add(specialAbility);
            return this;
        }
        public Builder setParticleEffect(BasicParticleType particleEffect) {
            this.particleEffect = particleEffect;
            return this;
        }

        //DO NOT USE WILL BE REMOVED
        public Builder setShouldNotSting(){
            this.shouldSting = true;
            return this;
        }

        public BeeTrait build() {
            return new BeeTrait(potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffect, shouldSting);
        }
    }
}
