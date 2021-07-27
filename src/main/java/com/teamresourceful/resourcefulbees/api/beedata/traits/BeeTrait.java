package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Unmodifiable
public class BeeTrait {
    public static final BeeTrait DEFAULT = new BeeTrait("this is a test string", null, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

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
                Codec.STRING.fieldOf("name").orElse(name).forGetter(BeeTrait::getName),
                Registry.ITEM.fieldOf("displayItem").orElse(ModItems.TRAIT_ICON.get()).forGetter(BeeTrait::getDisplayItem),
                CodecUtils.createSetCodec(PotionDamageEffect.CODEC).fieldOf("potionDamageEffects").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionDamageEffects),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("damageImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageImmunities),
                CodecUtils.createSetCodec(Registry.MOB_EFFECT).fieldOf("potionImmunities").orElse(new HashSet<>()).forGetter(BeeTrait::getPotionImmunities),
                CodecUtils.createSetCodec(DamageType.CODEC).fieldOf("damageTypes").orElse(new HashSet<>()).forGetter(BeeTrait::getDamageTypes),
                CodecUtils.createSetCodec(Codec.STRING).fieldOf("specialAbilities").orElse(new HashSet<>()).forGetter(BeeTrait::getSpecialAbilities),
                CodecUtils.createSetCodec(Registry.PARTICLE_TYPE).fieldOf("particleType").orElse(new HashSet<>()).forGetter(BeeTrait::getParticleEffects)
        ).apply(instance, BeeTrait::new));
    }

    protected String name;
    protected Item displayItem;
    protected final Set<PotionDamageEffect> potionDamageEffects;
    protected final Set<String> damageImmunities;
    protected final Set<Effect> potionImmunities;
    protected final Set<DamageType> damageTypes;
    protected final Set<String> specialAbilities;
    protected final Set<ParticleType<?>> particleEffects;

    protected BeeTrait(String name, Item displayItem, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<Effect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
        this.name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        this.displayItem = displayItem == null ? Items.BLAZE_POWDER : displayItem; //covers trait data object codec
        this.potionDamageEffects = potionDamageEffects;
        this.damageImmunities = damageImmunities;
        this.potionImmunities = potionImmunities;
        this.damageTypes = damageTypes;
        this.specialAbilities = specialAbilities;
        this.particleEffects = particleEffects;
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
     * @return Returns a {@link Set} of {@link PotionDamageEffect}s.
     */
    public Set<PotionDamageEffect> getPotionDamageEffects() { return potionDamageEffects; }

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
     * @return Returns a {@link Set} of {@link Effect}s.
     */
    public Set<Effect> getPotionImmunities() { return potionImmunities; }

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

    public BeeTrait toImmutable() {
        return this;
    }

    public static class Mutable extends BeeTrait {

        public Mutable(String name, Item displayItem, Set<PotionDamageEffect> potionDamageEffects, Set<String> damageImmunities, Set<Effect> potionImmunities, Set<DamageType> damageTypes, Set<String> specialAbilities, Set<ParticleType<?>> particleEffects) {
            super(name, displayItem, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects);
        }

        public Mutable() {
            super("error", null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        }

        public Mutable setName(String name) {
            this.name = name;
            return this;
        }

        public Mutable setDisplayItem(Item displayItem) {
            this.displayItem = displayItem;
            return this;
        }

        public Mutable addDamagePotionEffects(Set<PotionDamageEffect> potionDamageEffects) {
            this.potionDamageEffects.addAll(potionDamageEffects);
            return this;
        }

        public Mutable addDamagePotionEffect(PotionDamageEffect potionDamageEffect) {
            this.potionDamageEffects.add(potionDamageEffect);
            return this;
        }

        public Mutable addDamageImmunities(Collection<String> damageImmunities) {
            this.damageImmunities.addAll(damageImmunities);
            return this;
        }

        public Mutable addDamageImmunity(String damageImmunity) {
            this.damageImmunities.add(damageImmunity);
            return this;
        }

        public Mutable addPotionImmunities(Collection<Effect> potionImmunities) {
            this.potionImmunities.addAll(potionImmunities);
            return this;
        }

        public Mutable addPotionImmunity(Effect potionImmunity) {
            this.potionImmunities.add(potionImmunity);
            return this;
        }

        public Mutable addDamageTypes(Collection<DamageType> damageTypes) {
            this.damageTypes.addAll(damageTypes);
            return this;
        }

        public Mutable addDamageType(DamageType damageType) {
            this.damageTypes.add(damageType);
            return this;
        }

        public Mutable addSpecialAbilities(Collection<String> specialAbilities) {
            this.specialAbilities.addAll(specialAbilities);
            return this;
        }

        public Mutable addSpecialAbility(String specialAbility) {
            this.specialAbilities.add(specialAbility);
            return this;
        }

        public Mutable addParticleEffects(Collection<ParticleType<?>> particleEffect) {
            this.particleEffects.addAll(particleEffect);
            return this;
        }

        public Mutable addParticleEffect(ParticleType<?> particleEffect) {
            this.particleEffects.add(particleEffect);
            return this;
        }

        @Override
        public BeeTrait toImmutable() {
            return new BeeTrait(name, displayItem, potionDamageEffects, damageImmunities, potionImmunities, damageTypes, specialAbilities, particleEffects);
        }
    }
}
