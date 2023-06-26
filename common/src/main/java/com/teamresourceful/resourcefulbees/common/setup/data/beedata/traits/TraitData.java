package com.teamresourceful.resourcefulbees.common.setup.data.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.trait.Aura;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitDamageType;
import com.teamresourceful.resourcefulbees.api.data.trait.PotionEffect;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record TraitData(
        int auraRange, Set<String> traits, boolean hasTraits,
        Set<PotionEffect> potionDamageEffects,
        Set<String> damageImmunities,
        Set<MobEffect> potionImmunities,
        Set<TraitDamageType> damageTypes,
        Set<String> specialAbilities,
        Set<ParticleType<?>> particleEffects,
        Set<Aura> auras
) implements BeeTraitData {

    private static final BeeTraitData DEFAULT = TraitData.of(BeeConfig.defaultAuraRange, Collections.emptySet());
    private static final Codec<BeeTraitData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(3, 20).fieldOf("auraRange").orElse(BeeConfig.defaultAuraRange).forGetter(BeeTraitData::auraRange),
            CodecExtras.set(Codec.STRING).fieldOf("traits").orElse(new HashSet<>()).forGetter(BeeTraitData::traits)
    ).apply(instance, TraitData::of));
    public static final BeeDataSerializer<BeeTraitData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("trait"), 1, id -> CODEC, DEFAULT);

    public static TraitData of(int range, Set<String> traits) {
        Set<PotionEffect> potionDamageEffects = new HashSet<>();
        Set<String> damageImmunities = new HashSet<>();
        Set<MobEffect> potionImmunities = new HashSet<>();
        Set<TraitDamageType> damageTypes = new HashSet<>();
        Set<String> specialAbilities = new HashSet<>();
        Set<ParticleType<?>> particleEffects = new HashSet<>();
        Set<Aura> auras = new HashSet<>();

        for (String id : traits) {
            Trait trait = TraitRegistry.get().getTrait(id);
            if (trait == null) continue;
            potionDamageEffects.addAll(trait.potionDamageEffects());
            damageImmunities.addAll(trait.damageImmunities());
            potionImmunities.addAll(trait.potionImmunities());
            damageTypes.addAll(trait.damageTypes());
            specialAbilities.addAll(trait.specialAbilities());
            particleEffects.addAll(trait.particleEffects());
            auras.addAll(trait.auras());
        }

        boolean hasData = !potionDamageEffects.isEmpty() || !damageImmunities.isEmpty() || !potionImmunities.isEmpty() || !damageTypes.isEmpty() || !specialAbilities.isEmpty() || !particleEffects.isEmpty() || !auras.isEmpty();

        return new TraitData(
                range,
                traits,
                hasData,
                Collections.unmodifiableSet(potionDamageEffects),
                Collections.unmodifiableSet(damageImmunities),
                Collections.unmodifiableSet(potionImmunities),
                Collections.unmodifiableSet(damageTypes),
                Collections.unmodifiableSet(specialAbilities),
                Collections.unmodifiableSet(particleEffects),
                Collections.unmodifiableSet(auras)
        );
    }

    @Override
    public BeeDataSerializer<BeeTraitData> serializer() {
        return SERIALIZER;
    }
}
