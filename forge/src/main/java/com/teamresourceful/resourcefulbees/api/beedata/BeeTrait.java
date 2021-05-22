package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.*;

public class BeeTrait {

    protected final String name;
    protected final Item displayItem;
    protected final Set<PotionDamageEffect> potionDamageEffects;
    protected final Set<String> damageImmunities;
    protected final Set<MobEffect> potionImmunities;
    protected final Set<DamageType> damageTypes;
    protected final Set<String> specialAbilities;
    protected final Set<ParticleType<?>> particleEffects;

    private BeeTrait(String name, Item displayItem, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
        this.name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        this.displayItem = displayItem;
        this.potionDamageEffects = potionDamageEffects;
        this.damageImmunities = damageImmunities;
        this.potionImmunities = potionImmunities;
        this.damageTypes = damageTypes;
        this.specialAbilities = specialAbilities;
        this.particleEffects = particleEffects;
    }

    protected BeeTrait(String name, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
        this.name = name;
        this.displayItem = null;
        this.potionDamageEffects = potionDamageEffects;
        this.damageImmunities = damageImmunities;
        this.potionImmunities = potionImmunities;
        this.damageTypes = damageTypes;
        this.specialAbilities = specialAbilities;
        this.particleEffects = particleEffects;
    }

    public String getName() {
        return name;
    }

    public Item getDisplayItem() { return displayItem; }

    public Set<PotionDamageEffect> getPotionDamageEffects() { return potionDamageEffects; }

    public Set<String> getDamageImmunities() { return damageImmunities; }

    public Set<MobEffect> getPotionImmunities() { return potionImmunities; }

    public Set<DamageType> getDamageTypes() { return damageTypes; }

    public Set<String> getSpecialAbilities() { return specialAbilities; }

    public Set<ParticleType<?>> getParticleEffects() { return particleEffects; }

    public String getTranslationKey() {
        return String.format("trait.%s.%s", ResourcefulBees.MOD_ID, name);
    }

    public boolean hasDamagePotionEffects() { return !potionDamageEffects.isEmpty(); }
    public boolean hasDamageImmunities() { return !damageImmunities.isEmpty(); }
    public boolean hasPotionImmunities() { return !potionImmunities.isEmpty(); }
    public boolean hasDamageTypes() { return !damageTypes.isEmpty(); }
    public boolean hasSpecialAbilities() { return !specialAbilities.isEmpty(); }
    public boolean hasParticleEffects() { return !particleEffects.isEmpty(); }

    public static Codec<BeeTrait> getCodec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").orElse(name).forGetter(BeeTrait::getName),
                Registry.ITEM.fieldOf("displayItem").orElse(Items.BLAZE_POWDER).forGetter(BeeTrait::getDisplayItem),
                CodecUtils.createSetCodec(PotionDamageEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionDamageEffects),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageImmunities),
                CodecUtils.createSetCodec(Registry.MOB_EFFECT).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionImmunities),
                CodecUtils.createSetCodec(DamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageTypes),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(BeeTrait::getSpecialAbilities),
                CodecUtils.createSetCodec(Registry.PARTICLE_TYPE).fieldOf("particleType").orElse(new HashSet<>()).forGetter(BeeTrait::getParticleEffects)
        ).apply(instance, BeeTrait::new));
    }

    public static class Builder {
        final String name;
        Item beepediaItem = Items.BLAZE_POWDER;
        final Set<PotionDamageEffect> potionDamageEffects = new HashSet<>();
        final Set<String> damageImmunities = new HashSet<>();
        final Set<MobEffect> potionImmunities = new HashSet<>();
        final Set<DamageType> damageTypes = new HashSet<>();
        final Set<String> specialAbilities = new HashSet<>();
        final Set<ParticleType<?>> particleEffects = new HashSet<>();

        public Builder(String name) {
            this.name = name;
        }

        public BeeTrait.Builder setBeepediaItem(Item beepediaItem) {
            this.beepediaItem = beepediaItem;
            return this;
        }

        public BeeTrait.Builder addDamagePotionEffects(Set<PotionDamageEffect> potionDamageEffects) {
            this.potionDamageEffects.addAll(potionDamageEffects);
            return this;
        }

        public BeeTrait.Builder addDamagePotionEffect(PotionDamageEffect potionDamageEffect) {
            this.potionDamageEffects.add(potionDamageEffect);
            return this;
        }

        public BeeTrait.Builder addDamageImmunities(Collection<String> damageImmunities) {
            this.damageImmunities.addAll(damageImmunities);
            return this;
        }

        public BeeTrait.Builder addDamageImmunity(String damageImmunity) {
            this.damageImmunities.add(damageImmunity);
            return this;
        }

        public BeeTrait.Builder addPotionImmunities(Collection<MobEffect> potionImmunities) {
            this.potionImmunities.addAll(potionImmunities);
            return this;
        }

        public BeeTrait.Builder addPotionImmunity(MobEffect potionImmunity) {
            this.potionImmunities.add(potionImmunity);
            return this;
        }

        public BeeTrait.Builder addDamageTypes(Collection<DamageType> damageTypes) {
            this.damageTypes.addAll(damageTypes);
            return this;
        }

        public BeeTrait.Builder addDamageType(DamageType damageType) {
            this.damageTypes.add(damageType);
            return this;
        }

        public BeeTrait.Builder addSpecialAbilities(Collection<String> specialAbilities) {
            this.specialAbilities.addAll(specialAbilities);
            return this;
        }

        public BeeTrait.Builder addSpecialAbility(String specialAbility) {
            this.specialAbilities.add(specialAbility);
            return this;
        }

        public BeeTrait.Builder addParticleEffects(Collection<ParticleType<?>> particleEffect) {
            this.particleEffects.addAll(particleEffect);
            return this;
        }

        public BeeTrait.Builder addParticleEffect(ParticleType<?> particleEffect) {
            this.particleEffects.add(particleEffect);
            return this;
        }

        public BeeTrait build() {
            return new BeeTrait(name, beepediaItem, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects);
        }
    }
}
