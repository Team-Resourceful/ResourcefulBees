package com.resourcefulbees.resourcefulbees.data;

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
        private String effectID;
        private int strength;
        @Deprecated
        private String effectRegistryName;
        @Deprecated
        private int amplifier;

        public String getEffectID() {
            return effectRegistryName != null && !effectRegistryName.isEmpty() ? effectRegistryName : effectID;
        }

        public int getStrength() {
            return amplifier > 0 ? amplifier : strength;
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
