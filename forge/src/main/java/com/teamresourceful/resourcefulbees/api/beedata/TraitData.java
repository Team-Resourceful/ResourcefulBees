package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.registry.TraitRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TraitData extends BeeTrait {

    public static Codec<TraitData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("hasTraits").orElse(false).forGetter(TraitData::hasTraits),
                Codec.STRING.fieldOf("name").orElse(name).forGetter(BeeTrait::getName),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("traits").orElse(new HashSet<>()).forGetter(TraitData::getTraits),
                CodecUtils.createSetCodec(PotionDamageEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(TraitData::getPotionDamageEffects),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(TraitData::getDamageImmunities),
                CodecUtils.createSetCodec(Registry.MOB_EFFECT).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(TraitData::getPotionImmunities),
                CodecUtils.createSetCodec(DamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(TraitData::getDamageTypes),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(TraitData::getSpecialAbilities),
                CodecUtils.createSetCodec(Registry.PARTICLE_TYPE).fieldOf("particleType").forGetter(TraitData::getParticleEffects)
        ).apply(instance, TraitData::new));
    }

    private final Set<String> traits;
    private final boolean hasTraits;

    private TraitData(boolean hasTraits, String name, Set<String> traits, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
        super(name, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects);
        this.hasTraits = hasTraits;
        this.traits = traits;
        this.traits.forEach(this::addTrait);
    }

    private void addTrait(String traitName) {
        addTraitData(TraitRegistry.getRegistry().getTrait(traitName));
    }

    private void addTraitData(BeeTrait trait){
        potionDamageEffects.addAll(trait.getPotionDamageEffects());
        damageImmunities.addAll(trait.getDamageImmunities());
        potionImmunities.addAll(trait.getPotionImmunities());
        damageTypes.addAll(trait.getDamageTypes());
        specialAbilities.addAll(trait.getSpecialAbilities());
        particleEffects.addAll(trait.getParticleEffects());
    }

    public Set<String> getTraits() {
        return traits;
    }

    public boolean hasTraits() { return hasTraits; }


    public static TraitData createDefault() {
        return new TraitData(false, "null", Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }
}
