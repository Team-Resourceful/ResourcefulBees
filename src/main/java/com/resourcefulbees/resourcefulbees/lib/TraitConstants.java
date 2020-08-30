package com.resourcefulbees.resourcefulbees.lib;

import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;

public class TraitConstants {
    public static final String POTION_EFFECT = "potionEffect";
    public static final String POTION_EFFECT_AMPLIFIER = "potionAmplifier";
    public static final String POTION_EFFECTS = "potionEffects";

    public static final String DAMAGE_IMMUNITIES = "damageImmunities";
    public static final String DAMAGE_TYPE = "damageType";
    public static final String DAMAGE_AMPLIFIER = "damageAmplifier";

    public static final String DAMAGE_TYPES = "damageTypes";
    public static final String ABILITY_TYPES = "abilityTypes";

    public static final String POTION_IMMUNITIES = "potionImmunities";
    public static final String PARTICLE_EFFECT = "particleEffect";


    public static final CompoundNBT WITHER = BeeTrait.builder()
            .addPotionImmunities(Collections.singletonList(Effects.WITHER))
            .addPotionEffects(Collections.singletonList(Pair.of(Effects.WITHER, 1)))
            .build();
    public static final CompoundNBT BLAZE = BeeTrait.builder()
            .addDamageSourceImmunities(Arrays.asList(DamageSource.LAVA,DamageSource.IN_FIRE,DamageSource.ON_FIRE,DamageSource.HOT_FLOOR))
            .addDamageTypes(Collections.singletonList(Pair.of("setOnFire", 1)))
            .addSpecialAbilities(Collections.singletonList("flammable"))
            .addParticleEffects(ParticleTypes.FLAME)
            .build();
    public static final CompoundNBT NETHER = BeeTrait.builder()
            .addDamageSourceImmunities(Arrays.asList(DamageSource.LAVA,DamageSource.IN_FIRE,DamageSource.ON_FIRE,DamageSource.HOT_FLOOR))
            .build();
    public static final CompoundNBT CREEPER = BeeTrait.builder()
            .addDamageTypes(Collections.singletonList(Pair.of("explosive", 4)))
            .build();
    public static final CompoundNBT ZOMBIE = BeeTrait.builder()
            .addPotionEffects(Collections.singletonList(Pair.of(Effects.HUNGER, 20)))
            .build();
    public static final CompoundNBT PIGMAN = BeeTrait.builder()
            .addPotionEffects(Collections.singletonList(Pair.of(Effects.MINING_FATIGUE, 0)))
            .build();
    public static final CompoundNBT CANSWIM = BeeTrait.builder()
            .addDamageSourceImmunities(Collections.singletonList(DamageSource.DROWN))
            .build();
    public static final CompoundNBT ENDER = BeeTrait.builder()
            .addSpecialAbilities(Collections.singletonList("teleport"))
            .addParticleEffects(ParticleTypes.PORTAL)
            .build();
}
