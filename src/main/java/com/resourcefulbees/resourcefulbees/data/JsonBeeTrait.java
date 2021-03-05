package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;

import java.util.List;

public class JsonBeeTrait {

    //TODO 1.17 update class names due to json file changes

    private JsonBeeTrait() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static class JsonTrait {
        public List<PotionDamageEffect> potionDamageEffects;
        public String[] damageImmunities;
        public String[] potionImmunities;
        public List<DamageType> damageTypes;
        public String[] specialAbilities;
        public String particleName;
    }

    public static class PotionDamageEffect {
        public String effectID;
        public int strength;
    }

    public static class DamageType {
        public String damageType;
        public int duration;
    }
}
