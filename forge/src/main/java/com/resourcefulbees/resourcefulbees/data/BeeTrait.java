package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BeeTrait {
    private final Item beepediaItem;
    private final List<Pair<MobEffect, Integer>> potionDamageEffects;
    private final List<DamageSource> damageImmunities;
    private final List<MobEffect> potionImmunities;
    private final List<Pair<String, Integer>> damageTypes;
    private final List<String> specialAbilities;
    private final SimpleParticleType particleEffect;
    private final String name;

    private BeeTrait(String name, Item beepediaItem, List<Pair<MobEffect, Integer>> potionDamageEffects, List<DamageSource> damageImmunities, List<MobEffect> potionImmunities, List<Pair<String, Integer>> damageTypes, List<String> specialAbilities, SimpleParticleType particleEffect) {
        this.name = name;
        this.beepediaItem = beepediaItem;
        this.potionDamageEffects = potionDamageEffects;
        this.damageImmunities = damageImmunities;
        this.potionImmunities = potionImmunities;
        this.damageTypes = damageTypes;
        this.specialAbilities = specialAbilities;
        this.particleEffect = particleEffect;
    }

    public boolean hasDamagePotionEffects() {
        return this.potionDamageEffects != null && !this.potionDamageEffects.isEmpty();
    }

    public boolean hasDamageImmunities() {
        return this.damageImmunities != null && !this.damageImmunities.isEmpty();
    }

    public boolean hasPotionImmunities() {
        return this.potionImmunities != null && !this.potionImmunities.isEmpty();
    }

    public boolean hasDamageTypes() {
        return this.damageTypes != null && !this.damageTypes.isEmpty();
    }

    public boolean hasSpecialAbilities() {
        return this.specialAbilities != null && !this.specialAbilities.isEmpty();
    }

    public boolean hasParticleEffect() {
        return this.particleEffect != null;
    }

    public List<Pair<MobEffect, Integer>> getPotionDamageEffects() {
        return this.potionDamageEffects;
    }

    public List<DamageSource> getDamageImmunities() {
        return this.damageImmunities;
    }

    public List<MobEffect> getPotionImmunities() {
        return this.potionImmunities;
    }

    public List<Pair<String, Integer>> getDamageTypes() {
        return this.damageTypes;
    }

    public List<String> getSpecialAbilities() {
        return this.specialAbilities;
    }

    public SimpleParticleType getParticleEffect() {
        return this.particleEffect;
    }

    public Item getBeepediaItem() {
        return beepediaItem == null ? Items.BLAZE_POWDER : beepediaItem;
    }

    public String getTranslationKey() {
        return String.format("trait.%s.%s", ResourcefulBees.MOD_ID, name);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        String name;
        Item beepediaItem;
        List<Pair<MobEffect, Integer>> potionDamageEffects = new ArrayList<>();
        List<DamageSource> damageImmunities = new ArrayList<>();
        List<MobEffect> potionImmunities = new ArrayList<>();
        List<Pair<String, Integer>> damageTypes = new ArrayList<>();
        List<String> specialAbilities = new ArrayList<>();
        SimpleParticleType particleEffect;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setBeepediaItem(Item beepediaItem) {
            this.beepediaItem = beepediaItem;
            return this;
        }

        public Builder addDamagePotionEffects(List<Pair<MobEffect, Integer>> potionDamageEffects) {
            this.potionDamageEffects.addAll(potionDamageEffects);
            return this;
        }

        public Builder addDamagePotionEffect(Pair<MobEffect, Integer> potionDamageEffect) {
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

        public Builder addPotionImmunities(List<MobEffect> potionImmunities) {
            this.potionImmunities.addAll(potionImmunities);
            return this;
        }

        public Builder addPotionImmunity(MobEffect potionImmunity) {
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

        public Builder setParticleEffect(SimpleParticleType particleEffect) {
            this.particleEffect = particleEffect;
            return this;
        }

        public BeeTrait build() {
            return new BeeTrait(name, beepediaItem, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffect);
        }
    }
}
