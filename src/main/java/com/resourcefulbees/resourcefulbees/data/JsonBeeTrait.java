package com.resourcefulbees.resourcefulbees.data;

import java.util.List;

public class JsonBeeTrait {

    public static class jsonTrait {
        public List<traitPotionDamageEffect> potionDamageEffects;
        public String[] damageImmunities;
        public String[] potionImmunities;
        public List<traitDamageType> damageTypes;
        public String[] specialAbilities;
        public String particleName;
    }

    public static class traitPotionDamageEffect {
        public String effectRegistryName;
        public int amplifier;
    }

    public static class traitDamageType {
        public String damageTypeName;
        public int amplifier;
    }
}
