package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CombatData(boolean isPassive, double attackDamage, boolean removeStingerOnAttack, boolean inflictsPoison, double baseHealth, double armor, double armorToughness, double knockback, boolean isInvulnerable) {
    public static final CombatData DEFAULT = new CombatData(false, 1.0d, true, true, 10.0d, 0.0d, 0.0d, 0.0d, false);

    /**
     * A {@link Codec<CombatData>} that can be parsed to create a
     * {@link CombatData} object.
     */
    public static final Codec<CombatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isPassive").orElse(false).forGetter(CombatData::isPassive),
            Codec.doubleRange(1, Double.MAX_VALUE).fieldOf("attackDamage").orElse(1.0d).forGetter(CombatData::attackDamage),
            Codec.BOOL.fieldOf("removeStingerOnAttack").orElse(true).forGetter(CombatData::removeStingerOnAttack),
            Codec.BOOL.fieldOf("inflictsPoison").orElse(true).forGetter(CombatData::inflictsPoison),
            Codec.doubleRange(1, Double.MAX_VALUE).fieldOf("baseHealth").orElse(10.0d).forGetter(CombatData::baseHealth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("armor").orElse(0.0d).forGetter(CombatData::armor),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("armorToughness").orElse(0.0d).forGetter(CombatData::armorToughness),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("knockback").orElse(0.0d).forGetter(CombatData::knockback),
            Codec.BOOL.fieldOf("isInvulnerable").orElse(true).forGetter(CombatData::isInvulnerable)
    ).apply(instance, CombatData::new));
}
