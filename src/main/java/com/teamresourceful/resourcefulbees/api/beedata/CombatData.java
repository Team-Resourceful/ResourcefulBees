package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class CombatData {
    public static final CombatData DEFAULT = new CombatData(false, 1.0d, true, true, 10.0d, 0.0d, 0.0d, 0.0d, false);

    /**
     * A {@link Codec<CombatData>} that can be parsed to create a
     * {@link CombatData} object.
     */
    public static final Codec<CombatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isPassive").orElse(false).forGetter(CombatData::isPassive),
            Codec.doubleRange(1, Double.MAX_VALUE).fieldOf("attackDamage").orElse(1.0d).forGetter(CombatData::getAttackDamage),
            Codec.BOOL.fieldOf("removeStingerOnAttack").orElse(true).forGetter(CombatData::removeStingerOnAttack),
            Codec.BOOL.fieldOf("inflictsPoison").orElse(true).forGetter(CombatData::inflictsPoison),
            Codec.doubleRange(1, Double.MAX_VALUE).fieldOf("baseHealth").orElse(10.0d).forGetter(CombatData::getBaseHealth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("armor").orElse(0.0d).forGetter(CombatData::getArmor),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("armorToughness").orElse(0.0d).forGetter(CombatData::getArmorToughness),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("knockback").orElse(0.0d).forGetter(CombatData::getKnockback),
            Codec.BOOL.fieldOf("isInvulnerable").orElse(true).forGetter(CombatData::inflictsPoison)
    ).apply(instance, CombatData::new));

    protected double baseHealth;
    protected double attackDamage;
    protected boolean removeStingerOnAttack;
    protected boolean isPassive;
    protected boolean inflictsPoison;
    protected double armor;
    protected double armorToughness;
    protected double knockback;
    protected boolean isInvulnerable;

    private CombatData(boolean isPassive, double attackDamage, boolean removeStingerOnAttack, boolean inflictsPoison, double baseHealth, double armor, double armorToughness, double knockback, boolean isInvulnerable) {
        this.isPassive = isPassive;
        this.attackDamage = attackDamage;
        this.removeStingerOnAttack = removeStingerOnAttack;
        this.inflictsPoison = inflictsPoison;
        this.baseHealth = baseHealth;
        this.armor = armor;
        this.armorToughness = armorToughness;
        this.knockback = knockback;
        this.isInvulnerable = isInvulnerable;
    }

    /**
     *
     * @return Returns the base health amount for the bee before any
     * modifications are made.
     */
    public double getBaseHealth() {
        return baseHealth;
    }

    /**
     *
     * @return Returns the base attack damage amount for the bee before any
     * modifications are made.
     */
    public double getAttackDamage() {
        return attackDamage;
    }

    /**
     *
     * @return Returns <tt>true</tt>> if the bees stinger should be removed after attacking.
     */
    public boolean removeStingerOnAttack() {
        return removeStingerOnAttack;
    }

    /**
     *
     * @return Returns <tt>true</tt>> if the associated bee gets angry and attacks players.
     */
    public boolean isPassive() {
        return isPassive;
    }

    /**
     *
     * @return Returns <tt>true</tt>> if the associated bee inflicts poison when attacking players.
     */
    public boolean inflictsPoison() {
        return inflictsPoison;
    }

    /**
     *
     * @return Returns armor amount the associated bee has before any
     * modifications are made.
     */
    public double getArmor() {
        return armor;
    }

    /**
     *
     * @return Returns armor amount the associated bee has before any
     * modifications are made.
     */
    public double getArmorToughness() {
        return armorToughness;
    }

    /**
     *
     * @return Returns Knockback amount the associated bee has before any
     * modifications are made.
     */
    public double getKnockback() {
        return knockback;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public CombatData toImmutable() {
        return this;
    }

    public static class Mutable extends CombatData {
        public Mutable(boolean isPassive, double attackDamage, boolean removeStingerOnAttack, boolean inflictsPoison, double baseHealth, double armor, double armorToughness, double knockback, boolean isInvulnerable) {
            super(isPassive, attackDamage, removeStingerOnAttack, inflictsPoison, baseHealth, armor, armorToughness, knockback, isInvulnerable);
        }

        public Mutable() {
            super(false, 1, true, true, 10, 0, 0, 0, false);
        }

        public Mutable setBaseHealth(double baseHealth) {
            this.baseHealth = baseHealth;
            return this;
        }

        public Mutable setAttackDamage(double attackDamage) {
            this.attackDamage = attackDamage;
            return this;
        }

        public Mutable setRemoveStingerOnAttack(boolean removeStingerOnAttack) {
            this.removeStingerOnAttack = removeStingerOnAttack;
            return this;
        }

        public Mutable setPassive(boolean passive) {
            isPassive = passive;
            return this;
        }

        public Mutable setInflictsPoison(boolean inflictsPoison) {
            this.inflictsPoison = inflictsPoison;
            return this;
        }

        public Mutable setArmor(double armor) {
            this.armor = armor;
            return this;
        }

        public Mutable setArmorToughness(double armorToughness) {
            this.armorToughness = armorToughness;
            return this;
        }

        public Mutable setKnockback(double knockback) {
            this.knockback = knockback;
            return this;
        }

        public Mutable setIsInvulnerable(boolean isInvulnerable) {
            this.isInvulnerable = isInvulnerable;
            return this;
        }

        @Override
        public CombatData toImmutable() {
            return this;
        }
    }
}
