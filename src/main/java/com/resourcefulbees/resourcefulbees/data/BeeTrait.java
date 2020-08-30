package com.resourcefulbees.resourcefulbees.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.resourcefulbees.resourcefulbees.lib.TraitConstants.*;

public class BeeTrait {

    private static final HashMap<String, DamageSource> DAMAGE_SOURCE_MAP = new HashMap<>();

    public BeeTrait () {}

    public static Builder builder() {
        return new Builder();
    }

    public static List<Pair<Effect, Integer>> getPotionEffects(CompoundNBT nbt){
        List<Pair<Effect, Integer>> potionEffects = new ArrayList<>();
        ListNBT potions = nbt.getList(POTION_EFFECTS, 10);
        for (int i = 0; i < potions.size(); i++) {
            CompoundNBT potionNbtPair = potions.getCompound(i);
            Effect potionEffect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionNbtPair.getString(POTION_EFFECT)));
            if (potionEffect !=null){
                potionEffects.add(Pair.of(potionEffect, potionNbtPair.getInt(POTION_EFFECT_AMPLIFIER)));
            }
        }
        return potionEffects;
    }

    public static List<DamageSource> getDamageImmunities(CompoundNBT nbt){
        List<DamageSource> damageImmunities = new ArrayList<>();
        ListNBT damageImmunitiesNbtList = nbt.getList(DAMAGE_IMMUNITIES, 8);
        for (int i = 0; i < damageImmunitiesNbtList.size(); i++) {
            damageImmunities.add(DAMAGE_SOURCE_MAP.get(damageImmunitiesNbtList.getString(i)));
        }
        return damageImmunities;
    }

    public static List<Effect> getPotionImmunities(CompoundNBT nbt){
        List<Effect> potionImmunities = new ArrayList<>();
        ListNBT potionImmunitiesNbtList = nbt.getList(POTION_IMMUNITIES, 8);
        for (int i = 0; i < potionImmunitiesNbtList.size(); i++) {
            Effect potionEffect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionImmunitiesNbtList.getString(i)));
            if (potionEffect !=null){
                potionImmunities.add(potionEffect);
            }
        }
        return potionImmunities;
    }

    public static List<Pair<String, Integer>> getDamageTypes(CompoundNBT nbt){
        List<Pair<String, Integer>> damageTypes = new ArrayList<>();
        ListNBT damageTypeList = nbt.getList(DAMAGE_TYPES, 10);
        for (int i = 0; i < damageTypeList.size(); i++) {
            CompoundNBT damageTypeNbtPair = damageTypeList.getCompound(i);
            damageTypes.add(Pair.of(damageTypeNbtPair.getString(DAMAGE_TYPE), damageTypeNbtPair.getInt(DAMAGE_AMPLIFIER)));
        }
        return damageTypes;
    }

    public static List<String> getSpecialAbilities(CompoundNBT nbt){
        List<String> abilities = new ArrayList<>();
        ListNBT abilityList = nbt.getList(ABILITY_TYPES, 8);
        for (int i = 0; i < abilityList.size(); i++) {
            abilities.add(abilityList.getString(i));
        }
        return abilities;
    }

    public static BasicParticleType getParticleEffect(CompoundNBT nbt){
        return (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(nbt.getString(PARTICLE_EFFECT)));
    }

    public static boolean hasPotionEffects(CompoundNBT nbt){
        ListNBT potions = nbt.getList(POTION_EFFECTS, 10);
        return potions.size() > 0;
    }

    public static boolean hasPotionImmunities(CompoundNBT nbt){
        ListNBT potions = nbt.getList(POTION_IMMUNITIES, 8);
        return potions.size() > 0;
    }

    public static boolean hasDamageImmunities(CompoundNBT nbt){
        ListNBT damageImmunitiesNbtList = nbt.getList(DAMAGE_IMMUNITIES, 8);
        return damageImmunitiesNbtList.size() > 0;
    }

    public static boolean hasDamageTypes(CompoundNBT nbt){
        ListNBT damageTypeList = nbt.getList(DAMAGE_TYPES, 10);
        return damageTypeList.size() > 0;
    }

    public static boolean hasSpecialAbilities(CompoundNBT nbt){
        ListNBT abilityList = nbt.getList(ABILITY_TYPES, 8);
        return abilityList.size() > 0;
    }

    public static boolean hasParticleEffects(CompoundNBT nbt){
        return nbt.contains(PARTICLE_EFFECT);
    }

    public static class Builder {

        CompoundNBT traitData = new CompoundNBT();

        public BeeTrait.Builder addPotionEffects(List<Pair<Effect, Integer>> effectList) {
            ListNBT potionEffects = new ListNBT();
            for (Pair<Effect, Integer> effectIntegerPair : effectList) {
                CompoundNBT potion = new CompoundNBT();
                potion.putString(POTION_EFFECT, Objects.requireNonNull(effectIntegerPair.getLeft().getRegistryName()).toString());
                potion.putInt(POTION_EFFECT_AMPLIFIER,effectIntegerPair.getRight());
                potionEffects.add(potion);
            }
            traitData.put(POTION_EFFECTS, potionEffects);
            return this;
        }

        public BeeTrait.Builder addPotionImmunities(List<Effect> effectList) {
            ListNBT potionEffects = new ListNBT();
            for (Effect effect : effectList) {
                potionEffects.add(StringNBT.valueOf(Objects.requireNonNull(effect.getRegistryName()).toString()));
            }
            traitData.put(POTION_IMMUNITIES, potionEffects);
            return this;
        }


        public BeeTrait.Builder addDamageSourceImmunities (List<DamageSource> sources) {
            ListNBT immunityList = new ListNBT();
            for (DamageSource damageSource : sources) {
                DAMAGE_SOURCE_MAP.put(damageSource.getDamageType(), damageSource);
                immunityList.add(StringNBT.valueOf(damageSource.getDamageType()));
            }
            traitData.put(DAMAGE_IMMUNITIES, immunityList);
            return this;
        }

        public BeeTrait.Builder addDamageTypes(List<Pair<String, Integer>> damageTypes) {
            ListNBT damageList = new ListNBT();
            for (Pair<String, Integer> damageType : damageTypes) {
                CompoundNBT damage = new CompoundNBT();
                damage.putString(DAMAGE_TYPE, damageType.getLeft());
                damage.putInt(DAMAGE_AMPLIFIER,damageType.getRight());
                damageList.add(damage);
            }
            traitData.put(DAMAGE_TYPES, damageList);
            return this;
        }

        public BeeTrait.Builder addSpecialAbilities(List<String> abilities){
            ListNBT abilityList = new ListNBT();
            for (String ability : abilities) {
                abilityList.add(StringNBT.valueOf(ability));
            }
            traitData.put(ABILITY_TYPES, abilityList);
            return this;
        }

        public BeeTrait.Builder addParticleEffects(BasicParticleType particle){
            traitData.putString(PARTICLE_EFFECT, Objects.requireNonNull(particle.getRegistryName()).toString());
            return this;
        }

        public CompoundNBT build() {
            return traitData;
        }
    }

}