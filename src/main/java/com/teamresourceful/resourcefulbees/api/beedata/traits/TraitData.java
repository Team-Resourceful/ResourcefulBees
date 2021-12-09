package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Unmodifiable
public class TraitData extends BeeTrait {
    public static final TraitData DEFAULT = new TraitData("this is a test string", Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

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
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("traits").orElse(new HashSet<>()).forGetter(TraitData::getTraits),
                CodecUtils.createSetCodec(PotionDamageEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionDamageEffects),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageImmunities),
                CodecUtils.createSetCodec(Registry.MOB_EFFECT.byNameCodec()).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionImmunities),
                CodecUtils.createSetCodec(DamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageTypes),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(BeeTrait::getSpecialAbilities),
                CodecUtils.createSetCodec(Registry.PARTICLE_TYPE.byNameCodec()).fieldOf("particles").orElse(new HashSet<>()).forGetter(BeeTrait::getParticleEffects)
        ).apply(instance, TraitData::new));
    }

    protected Set<String> traits;
    private final boolean hasTraits;

    private TraitData(String name, Set<String> traits, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
        super(name, null, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects);
        this.traits = traits;
        this.traits.forEach(this::addTrait);
        this.hasTraits = setHasTraits();
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

    @Override
    public TraitData toImmutable() {
        return this;
    }

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
    }
    //endregion

    public static class Mutable extends TraitData {
        public Mutable(String name, Set<String> traits, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
            super(name, traits, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects);
        }

        public Mutable() {
            super("error", new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        }

        public TraitData.Mutable setTraits(Set<String> traits) {
            this.traits = traits;
            return this;
        }

        public TraitData.Mutable setName(String name) {
            this.name = name;
            return this;
        }

        public TraitData.Mutable setDisplayItem(Item displayItem) {
            this.displayItem = displayItem;
            return this;
        }

        public TraitData.Mutable addDamagePotionEffects(Set<PotionDamageEffect> potionDamageEffects) {
            this.potionDamageEffects.addAll(potionDamageEffects);
            return this;
        }

        public TraitData.Mutable addDamagePotionEffect(PotionDamageEffect potionDamageEffect) {
            this.potionDamageEffects.add(potionDamageEffect);
            return this;
        }

        public TraitData.Mutable addDamageImmunities(Collection<String> damageImmunities) {
            this.damageImmunities.addAll(damageImmunities);
            return this;
        }

        public TraitData.Mutable addDamageImmunity(String damageImmunity) {
            this.damageImmunities.add(damageImmunity);
            return this;
        }

        public TraitData.Mutable addPotionImmunities(Collection<MobEffect> potionImmunities) {
            this.potionImmunities.addAll(potionImmunities);
            return this;
        }

        public TraitData.Mutable addPotionImmunity(MobEffect potionImmunity) {
            this.potionImmunities.add(potionImmunity);
            return this;
        }

        public TraitData.Mutable addDamageTypes(Collection<DamageType> damageTypes) {
            this.damageTypes.addAll(damageTypes);
            return this;
        }

        public TraitData.Mutable addDamageType(DamageType damageType) {
            this.damageTypes.add(damageType);
            return this;
        }

        public TraitData.Mutable addSpecialAbilities(Collection<String> specialAbilities) {
            this.specialAbilities.addAll(specialAbilities);
            return this;
        }

        public TraitData.Mutable addSpecialAbility(String specialAbility) {
            this.specialAbilities.add(specialAbility);
            return this;
        }

        public TraitData.Mutable addParticleEffects(Collection<ParticleType<?>> particleEffect) {
            this.particleEffects.addAll(particleEffect);
            return this;
        }

        public TraitData.Mutable addParticleEffect(ParticleType<?> particleEffect) {
            this.particleEffects.add(particleEffect);
            return this;
        }

        @Override
        public TraitData toImmutable() {
            return new TraitData(this.name, this.traits, this.potionDamageEffects, this.damageImmunities, this.potionImmunities, this.damageTypes, this.specialAbilities, this.particleEffects);
        }
    }
}
