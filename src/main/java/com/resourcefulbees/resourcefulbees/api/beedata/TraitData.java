package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TraitData extends AbstractBeeData {
    private transient List<Pair<Effect, Integer>> potionDamageEffects = new ArrayList<>();
    private transient List<DamageSource> damageImmunities = new ArrayList<>();
    private transient List<Effect> potionImmunities = new ArrayList<>();
    private transient List<Pair<String, Integer>> damageTypes = new ArrayList<>();
    private transient List<String> specialAbilities = new ArrayList<>();
    private transient List<BasicParticleType> particleEffects = new ArrayList<>();
    private transient boolean shouldSting = true;
    private final boolean hasTraits;

    public TraitData(boolean hasTraits) {
        this.hasTraits = hasTraits;
    }

    public void addTrait(BeeTrait trait){
        if (trait != null) {
            if (trait.hasDamagePotionEffects())
                if (!this.hasDamagePotionEffects())
                    this.potionDamageEffects = trait.getPotionDamageEffects();
                else this.potionDamageEffects.addAll(trait.getPotionDamageEffects());
            if (trait.hasDamageImmunities())
                if (!this.hasDamageImmunities())
                    this.damageImmunities = trait.getDamageImmunities();
                else this.damageImmunities.addAll(trait.getDamageImmunities());
            if (trait.hasPotionImmunities())
                if (!this.hasPotionImmunities())
                    this.potionImmunities = trait.getPotionImmunities();
                else this.potionImmunities.addAll(trait.getPotionImmunities());
            if (trait.hasDamageTypes())
                if (!this.hasDamageTypes())
                    this.damageTypes = trait.getDamageTypes();
                else this.damageTypes.addAll(trait.getDamageTypes());
            if (trait.hasSpecialAbilities())
                if (!this.hasSpecialAbilities())
                    this.specialAbilities = trait.getSpecialAbilities();
                else this.specialAbilities.addAll(trait.getSpecialAbilities());
            if (trait.hasParticleEffect())
                if (!this.hasParticleEffects())
                    this.particleEffects = Collections.singletonList(trait.getParticleEffect());
                else this.particleEffects.add(trait.getParticleEffect());
            if (trait.shouldNotString() && shouldSting)
                this.shouldSting = false;
        }
    }

    public boolean hasTraits(){ return this.hasTraits; }

    public boolean hasDamagePotionEffects(){ return this.potionDamageEffects != null && !this.potionDamageEffects.isEmpty(); }
    public boolean hasDamageImmunities(){ return this.damageImmunities != null && !this.damageImmunities.isEmpty(); }
    public boolean hasPotionImmunities(){ return this.potionImmunities != null && !this.potionImmunities.isEmpty(); }
    public boolean hasDamageTypes(){ return this.damageTypes != null && !this.damageTypes.isEmpty(); }
    public boolean hasSpecialAbilities(){ return this.specialAbilities != null && !this.specialAbilities.isEmpty(); }
    public boolean hasParticleEffects(){ return this.particleEffects != null && !this.particleEffects.isEmpty(); }
    public boolean shouldSting(){ return shouldSting; }

    public List<Pair<Effect, Integer>> getPotionDamageEffects(){
        return potionDamageEffects;
    }
    public List<DamageSource> getDamageImmunities(){
        return damageImmunities;
    }
    public List<Effect> getPotionImmunities(){
        return potionImmunities;
    }
    public List<Pair<String, Integer>> getDamageTypes(){
        return damageTypes;
    }
    public List<String> getSpecialAbilities(){
        return specialAbilities;
    }
    public List<BasicParticleType> getParticleEffects(){
        return particleEffects;
    }
}
