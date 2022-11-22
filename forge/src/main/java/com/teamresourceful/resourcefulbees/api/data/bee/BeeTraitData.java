package com.teamresourceful.resourcefulbees.api.data.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.trait.Aura;
import com.teamresourceful.resourcefulbees.api.data.trait.DamageType;
import com.teamresourceful.resourcefulbees.api.data.trait.PotionEffect;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;

import java.util.Set;

public interface BeeTraitData extends BeeData<BeeTraitData> {

    int auraRange();

    Set<String> traits();
    Set<PotionEffect> potionDamageEffects();
    Set<String> damageImmunities();
    Set<MobEffect> potionImmunities();
    Set<DamageType> damageTypes();
    Set<String> specialAbilities();
    Set<ParticleType<?>> particleEffects();
    Set<Aura> auras();

    boolean hasTraits();

    default boolean hasAuras() {
        return !auras().isEmpty();
    }

    default boolean hasDamageTypes() {
        return !damageTypes().isEmpty();
    }

    default boolean hasDamageImmunities() {
        return !damageImmunities().isEmpty();
    }

    default boolean hasPotionImmunities() {
        return !potionImmunities().isEmpty();
    }

    default boolean hasPotionDamageEffects() {
        return !potionDamageEffects().isEmpty();
    }

    default boolean hasSpecialAbilities() {
        return !specialAbilities().isEmpty();
    }

    default boolean hasParticleEffects() {
        return !particleEffects().isEmpty();
    }

    default boolean canPoison() {
        return !hasDamageTypes() && !hasPotionDamageEffects();
    }

}
