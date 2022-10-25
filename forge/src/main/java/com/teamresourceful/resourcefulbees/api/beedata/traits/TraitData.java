package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Unmodifiable
public class TraitData extends BeeTrait {
    public static final TraitData DEFAULT = new TraitData("this is a test string", CommonConfig.DEFAULT_AURA_RANGE.get(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

    /**
     * Returns a {@link Codec<TraitData>} that can be parsed to create a
     * {@link TraitData} object. The name value passed in is a fallback value
     * usually obtained from the bee json file name.
     * <i>Note: Name is synonymous with "bee type"</i>
     *
     * @param name The name (or "bee type") for the bee.
     * @return Returns a {@link Codec<TraitData>} that can be parsed to
     * create a {@link TraitData} object.
     */
    public static Codec<TraitData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(BeeTrait::getName),
                Codec.intRange(3, 20).fieldOf("auraRange").orElse(CommonConfig.DEFAULT_AURA_RANGE.get()).forGetter(TraitData::getAuraRange),
                CodecExtras.set(Codec.STRING).fieldOf("traits").orElse(new HashSet<>()).forGetter(TraitData::getTraits),
                CodecExtras.set(PotionEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionDamageEffects),
                CodecExtras.set(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageImmunities),
                CodecExtras.set(Registry.MOB_EFFECT.byNameCodec()).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionImmunities),
                CodecExtras.set(DamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageTypes),
                CodecExtras.set(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(BeeTrait::getSpecialAbilities),
                CodecExtras.set(Registry.PARTICLE_TYPE.byNameCodec()).fieldOf("particles").orElse(new HashSet<>()).forGetter(BeeTrait::getParticleEffects),
                CodecExtras.set(BeeAura.CODEC).fieldOf("auras").orElse(new HashSet<>()).forGetter(BeeTrait::getAuras)
        ).apply(instance, TraitData::new));
    }

    protected int auraRange;
    protected Set<String> traits;
    private final boolean hasTraits;

    public TraitData(String name, int auraRange, Set<String> traits, Set<PotionEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects, Set<BeeAura> auras) {
        super(name, null, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects, auras);
        this.auraRange = auraRange;
        this.traits = traits;
        this.traits.forEach(this::addTrait);
        this.hasTraits = setHasTraits();
    }

    /**
     * Gets the range of a bees aura.
     *
     * @return Returns the range as an {@link Integer}.
     */
    public int getAuraRange() {
        return auraRange;
    }

    /**
     *
     * @return Returns a {@link Set} of {@link String}s listing the traits that the
     * associated bee has.
     */
    public Set<String> getTraits() {
        return traits;
    }

    /**
     *
     * @return Returns <tt>true</tt> if the traits set is not empty or if the bee json
     * has trait data specified.
     */
    public boolean hasTraits() { return hasTraits; }

    //region Setup
    private boolean setHasTraits() {
        return !traits.isEmpty() || hasPotionDamageEffects() || hasDamageImmunities() || hasPotionImmunities() || hasDamageTypes() || hasSpecialAbilities() || hasParticleEffects();
    }

    private void addTrait(String traitName) {
        addTraitData(TraitRegistry.getRegistry().getTrait(traitName));
    }

    private void addTraitData(BeeTrait trait){
        if (trait == null) return;
        potionDamageEffects.addAll(trait.getPotionDamageEffects());
        damageImmunities.addAll(trait.getDamageImmunities());
        potionImmunities.addAll(trait.getPotionImmunities());
        damageTypes.addAll(trait.getDamageTypes());
        specialAbilities.addAll(trait.getSpecialAbilities());
        particleEffects.addAll(trait.getParticleEffects());
        auras.addAll(trait.getAuras());
    }
    //endregion
}
