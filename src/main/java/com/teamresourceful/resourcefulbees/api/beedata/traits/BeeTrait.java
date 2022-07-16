package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Unmodifiable
public class BeeTrait {
    public static final BeeTrait DEFAULT = new BeeTrait("this is a test string", null, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

    /**
     * Returns a {@link Codec<BeeTrait>} that can be parsed to create a
     * {@link BeeTrait} object. The name value passed in is a fallback value
     * usually obtained trait json file name. However, Bees can have custom
     * defined data directly in their json and in those instances the name would
     * be the bees name or "bee type"
     *
     * @param name The trait name or bee name for bee defined traits.
     * @return Returns a {@link Codec<BeeTrait>} that can be parsed to
     * create a {@link BeeTrait} object.
     */
    public static Codec<BeeTrait> getCodec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(BeeTrait::getName),
                Registry.ITEM.byNameCodec().fieldOf("displayItem").orElse(Items.NETHER_STAR).forGetter(BeeTrait::getDisplayItem),
                CodecExtras.set(PotionEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionDamageEffects),
                CodecExtras.set(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageImmunities),
                CodecExtras.set(Registry.MOB_EFFECT.byNameCodec()).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionImmunities),
                CodecExtras.set(DamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageTypes),
                CodecExtras.set(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(BeeTrait::getSpecialAbilities),
                CodecExtras.set(Registry.PARTICLE_TYPE.byNameCodec()).fieldOf("particleType").orElse(new HashSet<>()).forGetter(BeeTrait::getParticleEffects),
                CodecExtras.set(BeeAura.CODEC).fieldOf("auras").orElse(new HashSet<>()).forGetter(BeeTrait::getAuras)
        ).apply(instance, BeeTrait::new));
    }

    protected String name;
    protected Item displayItem;
    protected final Set<PotionEffect> potionDamageEffects;
    protected final Set<String> damageImmunities;
    protected final Set<MobEffect> potionImmunities;
    protected final Set<DamageType> damageTypes;
    protected final Set<String> specialAbilities;
    protected final Set<ParticleType<?>> particleEffects;
    protected final Set<BeeAura> auras;

    protected BeeTrait(String name, Item displayItem, Set<PotionEffect> potionDamageEffects, Set<String> damageImmunities, Set<MobEffect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects, Set<BeeAura> auras) {
        this.name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
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
     *
     * @return Returns the trait name or bee name for bee-defined traits
     */
    public String getName() {
        return name;
    }

    /**
     * This display {@link Item} is primarily used in the beepedia.
     *
     * @return Returns an {@link Item} that can be used for display purposes.
     */
    public Item getDisplayItem() { return displayItem; }

    /**
     * Potion damage effects are status effects sustained by players when the bee
     * attacks them. The strength for the effect can be customized.
     *
     * @return Returns a {@link Set} of {@link PotionEffect}s.
     */
    public Set<PotionEffect> getPotionDamageEffects() { return potionDamageEffects; }

    /**
     * Damage immunities ar damage source immunities that the bee has
     * <br>
     * The options available are: inFire, lightningBolt, onFire, lava, hotFloor,
     * inWall, cramming, drown, starve, cactus, fall, flyIntoWall, generic,
     * magic, wither, anvil, fallingBlock, dragonBreath, dryout"
     *
     * @return Returns a {@link Set} of {@link String}s.
     */
    public Set<String> getDamageImmunities() { return damageImmunities; }

    /**
     * The same potion damage effects that bees can cause they can also have
     * immunity to.
     *
     * @return Returns a {@link Set} of {@link MobEffect}s.
     */
    public Set<MobEffect> getPotionImmunities() { return potionImmunities; }

    /**
     * Can be either 'setOnFire' or 'explosive' -
     * can be list but really there's only two choices soo
     *
     * Damage types can, however, have an amplifier amount.
     *
     * @return Returns a {@link Set} of {@link DamageType}s.
     */
    public Set<DamageType> getDamageTypes() { return damageTypes; }

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
    public Set<String> getSpecialAbilities() { return specialAbilities; }

    /**
     * The particle types specified here are constant particle effects
     * that don't take any outside factors into consideration like anger,
     * baby, etc.
     *
     * @return Returns a {@link Set} of {@link ParticleType}s.
     */
    public Set<ParticleType<?>> getParticleEffects() { return particleEffects; }

    public Set<BeeAura> getAuras() { return auras; }

    public String getTranslationKey() {
        return String.format("trait.%s.%s", ResourcefulBees.MOD_ID, name);
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasPotionDamageEffects() {
        return !potionDamageEffects.isEmpty();
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasDamageImmunities() {
        return !damageImmunities.isEmpty();
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasPotionImmunities() {
        return !potionImmunities.isEmpty();
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasDamageTypes() {
        return !damageTypes.isEmpty();
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasSpecialAbilities() {
        return !specialAbilities.isEmpty();
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasParticleEffects() {
        return !particleEffects.isEmpty();
    }

    /**
     *
     * @return Returns <tt>true</tt> if the internal set is not empty.
     */
    public boolean hasAuras() {
        return !auras.isEmpty();
    }

    /**
     * @return Returns <tt>true</tt> if there is no damageTypes and no potionDamageEffects.
     */
    public boolean canPoison() {
        return !hasDamageTypes() && !hasPotionDamageEffects();
    }
}
