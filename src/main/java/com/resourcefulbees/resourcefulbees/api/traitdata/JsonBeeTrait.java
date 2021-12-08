package com.resourcefulbees.resourcefulbees.api.traitdata;

import com.google.gson.annotations.SerializedName;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;

import java.util.List;

public class JsonBeeTrait {

    //TODO 1.17 update class names due to json file changes

    private JsonBeeTrait() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @SuppressWarnings("unused")
    public static class JsonTrait {
        private final String beepediaItemID;
        private final List<PotionDamageEffect> potionDamageEffects;
        private final String[] damageImmunities;
        private final String[] potionImmunities;
        private final List<DamageType> damageTypes;
        private final String[] specialAbilities;
        private final List<BeeAura> beeAuras;
        private final String particleName;

        public JsonTrait(String beepediaItemID, List<PotionDamageEffect> potionDamageEffects, String[] damageImmunities, String[] potionImmunities, List<DamageType> damageTypes, String[] specialAbilities, String particleName, List<BeeAura> beeAuras) {
            this.beepediaItemID = beepediaItemID;
            this.potionDamageEffects = potionDamageEffects;
            this.damageImmunities = damageImmunities;
            this.potionImmunities = potionImmunities;
            this.damageTypes = damageTypes;
            this.specialAbilities = specialAbilities;
            this.particleName = particleName;
            this.beeAuras = beeAuras;
        }

        public String getBeepediaItemID() { return beepediaItemID; }

        public List<PotionDamageEffect> getPotionDamageEffects() { return potionDamageEffects; }

        public String[] getDamageImmunities() { return damageImmunities; }

        public String[] getPotionImmunities() { return potionImmunities; }

        public List<DamageType> getDamageTypes() { return damageTypes; }

        public String[] getSpecialAbilities() { return specialAbilities; }

        public String getParticleName() { return particleName; }

        public List<BeeAura> getBeeAuras() { return beeAuras; }
    }

    @SuppressWarnings("unused")
    public static class PotionDamageEffect {
        @SerializedName(value="effectID", alternate={"effectRegistryName"})
        private String effectID;
        @SerializedName(value="strength", alternate={"amplifier"})
        private int strength;

        public String getEffectID() {
            return effectID;
        }

        public int getStrength() {
            return strength;
        }
    }

    @SuppressWarnings("unused")
    public static class DamageType {
        private String damageTypeName;
        private int amplifier;

        public String getDamageType() {
            return damageTypeName;
        }

        public int getAmplifier() {
            return amplifier;
        }
    }
}
