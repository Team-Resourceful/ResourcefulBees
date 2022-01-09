package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.api.traitdata.BeeAura;
import com.resourcefulbees.resourcefulbees.api.traitdata.BeeTrait;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class TraitData extends AbstractBeeData {
    /**
     * A list of Potion Effects and their strength.
     */
    private transient Set<Pair<Effect, Integer>> potionDamageEffects = new HashSet<>();

    /**
     * A list of damage sources the bee is immune to
     */
    private transient Set<String> damageImmunities = new HashSet<>();

    /**
     * A list of effects the bee is immune to
     */
    private transient Set<Effect> potionImmunities = new HashSet<>();

    /**
     * A list of damage types and their strength.
     */
    private transient Set<Pair<String, Integer>> damageTypes = new HashSet<>();

    /**
     * A list of special abilities the bee has
     */
    private transient Set<String> specialAbilities = new HashSet<>();
    private transient Set<BasicParticleType> particleEffects = new HashSet<>();


    private transient Set<BeeAura> beeAuras;


    /**
     * If the bee has traits
     */
    private final boolean hasTraits;

    public TraitData(boolean hasTraits) {
        super("TraitData");
        this.hasTraits = hasTraits;

    }

    public void addTrait(BeeTrait trait){
        if (trait != null) {
            if (trait.hasDamagePotionEffects()) {
                this.potionDamageEffects.addAll(trait.getPotionDamageEffects());
            }
            if (trait.hasDamageImmunities()) {
                this.damageImmunities.addAll(trait.getDamageImmunities());
            }
            if (trait.hasPotionImmunities()) {
                this.potionImmunities.addAll(trait.getPotionImmunities());
            }
            if (trait.hasDamageTypes()) {
                this.damageTypes.addAll(trait.getDamageTypes());
            }
            if (trait.hasSpecialAbilities()) {
                this.specialAbilities.addAll(trait.getSpecialAbilities());
            }
            if (trait.hasParticleEffect()) {
                this.particleEffects.add(trait.getParticleEffect());
            }
            if (trait.hasBeeAuras()) {
                this.beeAuras.addAll(trait.getAuras());
            }
        }
    }

    public void initializeTraitSets() {
        this.potionDamageEffects = new HashSet<>();
        this.damageImmunities = new HashSet<>();
        this.potionImmunities = new HashSet<>();
        this.damageTypes = new HashSet<>();
        this.specialAbilities = new HashSet<>();
        this.particleEffects = new HashSet<>();
        this.beeAuras = new HashSet<>();
    }

    public boolean hasTraits() { return this.hasTraits; }

    public boolean hasDamagePotionEffects() { return this.hasTraits && !this.potionDamageEffects.isEmpty(); }
    public boolean hasDamageImmunities() { return this.hasTraits &&  !this.damageImmunities.isEmpty(); }
    public boolean hasPotionImmunities() { return this.hasTraits && !this.potionImmunities.isEmpty(); }
    public boolean hasDamageTypes() { return this.hasTraits && !this.damageTypes.isEmpty(); }
    public boolean hasSpecialAbilities() { return this.hasTraits && !this.specialAbilities.isEmpty(); }
    public boolean hasParticleEffects() { return this.hasTraits && !this.particleEffects.isEmpty(); }
    public boolean hasBeeAuras() { return this.hasTraits && !this.beeAuras.isEmpty(); }


    public Set<Pair<Effect, Integer>> getPotionDamageEffects(){
        return potionDamageEffects;
    }
    public Set<String> getDamageImmunities(){
        return damageImmunities;
    }
    public Set<Effect> getPotionImmunities(){
        return potionImmunities;
    }
    public Set<Pair<String, Integer>> getDamageTypes(){
        return damageTypes;
    }
    public Set<String> getSpecialAbilities(){
        return specialAbilities;
    }
    public Set<BasicParticleType> getParticleEffects(){
        return particleEffects;
    }
    public Set<BeeAura> getBeeAuras(){
        return beeAuras;
    }

    public static TraitData createDefault() {
        TraitData traitData = new TraitData(false);
        traitData.initializeTraitSets();
        return traitData;
    }
}
