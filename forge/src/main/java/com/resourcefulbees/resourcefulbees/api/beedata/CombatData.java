package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CombatData {

    public static final Codec<CombatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isPassive").orElse(false).forGetter(CombatData::isPassive),
            Codec.DOUBLE.fieldOf("attackDamage").orElse(1.0d).forGetter(CombatData::getAttackDamage),
            Codec.BOOL.fieldOf("removeStingerOnAttack").orElse(true).forGetter(CombatData::removeStingerOnAttack),
            Codec.BOOL.fieldOf("inflictsPoison").orElse(true).forGetter(CombatData::inflictsPoison),
            Codec.DOUBLE.fieldOf("baseHealth").orElse(10.0d).forGetter(CombatData::getBaseHealth),
            Codec.DOUBLE.fieldOf("armor").orElse(0.0d).forGetter(CombatData::getArmor),
            Codec.DOUBLE.fieldOf("armorToughness").orElse(0.0d).forGetter(CombatData::getArmorToughness),
            Codec.DOUBLE.fieldOf("knockback").orElse(0.0d).forGetter(CombatData::getKnockback)
    ).apply(instance, CombatData::new));

    private final double baseHealth;
    private final double attackDamage;
    private final boolean removeStingerOnAttack;
    private final boolean isPassive;
    private final boolean inflictsPoison;
    private final double armor;
    private final double armorToughness;
    private final double knockback;

    private CombatData(boolean isPassive, double attackDamage, boolean removeStingerOnAttack, boolean inflictsPoison, double baseHealth, double armor, double armorToughness, double knockback) {
        this.isPassive = isPassive;
        this.attackDamage = attackDamage;
        this.removeStingerOnAttack = removeStingerOnAttack;
        this.inflictsPoison = inflictsPoison;
        this.baseHealth = baseHealth;
        this.armor = armor;
        this.armorToughness = armorToughness;
        this.knockback = knockback;
    }

    public double getBaseHealth() {
        return baseHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public boolean removeStingerOnAttack() {
        return removeStingerOnAttack;
    }

    public boolean isPassive() {
        return isPassive;
    }

    public boolean inflictsPoison() {
        return inflictsPoison;
    }

    public double getArmor() {
        return armor;
    }

    public double getArmorToughness() {
        return armorToughness;
    }

    public double getKnockback() {
        return knockback;
    }

    public static CombatData createDefault() {
        return new CombatData(false, 1.0d, true, true, 10.0d, 0.0d, 0.0d, 0.0d);
    }
}
