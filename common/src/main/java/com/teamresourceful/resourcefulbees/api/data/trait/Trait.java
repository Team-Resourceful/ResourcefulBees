package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public record Trait(String name, Item displayItem,
                    Set<PotionEffect> potionDamageEffects, Set<String> damageImmunities,
                    Set<MobEffect> potionImmunities, Set<TraitDamageType> damageTypes, Set<String> specialAbilities,
                    Set<ParticleType<?>> particleEffects, Set<Aura> auras
) {
    public static final Trait DEFAULT = new Trait("this is a test string", null, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

    /**
     * Returns a {@link Codec<Trait>} that can be parsed to create a
     * {@link Trait} object. The name value passed in is a fallback value
     * usually obtained trait json file name. However, Bees can have custom
     * defined data directly in their json and in those instances the name would
     * be the bees name or "bee type"
     *
     * @param name The trait name or bee name for bee defined traits.
     * @return Returns a {@link Codec<Trait>} that can be parsed to
     * create a {@link Trait} object.
     */
    public static Codec<Trait> getCodec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
            MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(Trait::name),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("displayItem").orElse(Items.NETHER_STAR).forGetter(Trait::displayItem),
            CodecExtras.set(PotionEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(Trait::potionDamageEffects),
            CodecExtras.set(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(Trait::damageImmunities),
            CodecExtras.set(BuiltInRegistries.MOB_EFFECT.byNameCodec()).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(Trait::potionImmunities),
            CodecExtras.set(TraitDamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(Trait::damageTypes),
            CodecExtras.set(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(Trait::specialAbilities),
            CodecExtras.set(BuiltInRegistries.PARTICLE_TYPE.byNameCodec()).fieldOf("particleType").orElse(new HashSet<>()).forGetter(Trait::particleEffects),
            CodecExtras.set(Aura.CODEC).fieldOf("auras").orElse(new HashSet<>()).forGetter(Trait::auras)
        ).apply(instance, Trait::new));
    }

    public Trait(String name, Item displayItem, Set<PotionEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<TraitDamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects, Set<Aura> auras) {
        this.name = name.toLowerCase(Locale.ROOT).replace(" ", "_");
        this.displayItem = displayItem == null ? Items.BLAZE_POWDER : displayItem; //covers trait data object codec
        this.potionDamageEffects = potionDamageEffects;
        this.damageImmunities = damageImmunities;
        this.potionImmunities = potionImmunities;
        this.damageTypes = damageTypes;
        this.specialAbilities = specialAbilities;
        this.particleEffects = particleEffects;
        this.auras = auras;
    }

    /**
     * @return Returns the trait name or bee name for bee-defined traits
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * This display {@link Item} is primarily used in the beepedia.
     *
     * @return Returns an {@link Item} that can be used for display purposes.
     */
    @Override
    public Item displayItem() {
        return displayItem;
    }

    /**
     * Potion damage effects are status effects sustained by players when the bee
     * attacks them. The strength for the effect can be customized.
     *
     * @return Returns a {@link Set} of {@link PotionEffect}s.
     */
    @Override
    public Set<PotionEffect> potionDamageEffects() {
        return potionDamageEffects;
    }

    /**
     * Damage immunities ar damage source immunities that the bee has
     * <br>
     * The options available are: inFire, lightningBolt, onFire, lava, hotFloor,
     * inWall, cramming, drown, starve, cactus, fall, flyIntoWall, generic,
     * magic, wither, anvil, fallingBlock, dragonBreath, dryout"
     *
     * @return Returns a {@link Set} of {@link String}s.
     */
    @Override
    public Set<String> damageImmunities() {
        return damageImmunities;
    }

    /**
     * The same potion damage effects that bees can cause they can also have
     * immunity to.
     *
     * @return Returns a {@link Set} of {@link MobEffect}s.
     */
    @Override
    public Set<MobEffect> potionImmunities() {
        return potionImmunities;
    }

    /**
     * Can be either 'setOnFire' or 'explosive' -
     * can be list but really there's only two choices soo
     * <p>
     * Damage types can, however, have an amplifier amount.
     *
     * @return Returns a {@link Set} of {@link TraitDamageType}s.
     */
    @Override
    public Set<TraitDamageType> damageTypes() {
        return damageTypes;
    }

    /**
     * Returns a list of special abilities custom coded in the mod.
     * Options are: <br>
     * <br><b>teleport</b> - The bee can randomly teleport up to 4 blocks away.
     * <br><b>flammable<b> - The bee has Blaze particle effects.
     * <br><b>slimy</b> - The bee has slime particles and slime sound effects when hitting players.
     * <br><b>angry</b> - The bee is always angry at players, unless the calming effect is applied.
     * <br><b>spider</b> - The bee can't get stuck in cobwebs.
     *
     * @return Returns a {@link Set} of {@link String}s.
     */
    @Override
    public Set<String> specialAbilities() {
        return specialAbilities;
    }

    /**
     * The particle types specified here are constant particle effects
     * that don't take any outside factors into consideration like anger,
     * baby, etc.
     *
     * @return Returns a {@link Set} of {@link ParticleType}s.
     */
    @Override
    public Set<ParticleType<?>> particleEffects() {
        return particleEffects;
    }

    public Component getDisplayName() {
        return Component.translatable("trait_type.resourcefulbees." + name);
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasPotionDamageEffects() {
        return !potionDamageEffects.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasDamageImmunities() {
        return !damageImmunities.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasPotionImmunities() {
        return !potionImmunities.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasDamageTypes() {
        return !damageTypes.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasSpecialAbilities() {
        return !specialAbilities.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasParticleEffects() {
        return !particleEffects.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasAuras() {
        return !auras.isEmpty();
    }
}
