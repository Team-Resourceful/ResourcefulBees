package com.resourcefulbees.resourcefulbees.data;

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
        public String beepediaItemID;
        public List<PotionDamageEffect> potionDamageEffects;
        public String[] damageImmunities;
        public String[] potionImmunities;
        public List<DamageType> damageTypes;
        public String[] specialAbilities;
        public String particleName;
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
