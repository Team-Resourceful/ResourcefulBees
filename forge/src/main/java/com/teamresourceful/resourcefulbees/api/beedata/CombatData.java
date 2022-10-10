package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;

public record CombatData(
        boolean isPassive, boolean removeStingerOnAttack, boolean inflictsPoison, boolean isInvulnerable,
        Map<Attribute, Double> attributes
) {

    public static final Map<Attribute, Double> DEFAULT_ATTRIBUTES = Util.make(new HashMap<>(), map -> {
        map.put(Attributes.MAX_HEALTH, 10d);
        map.put(Attributes.FLYING_SPEED, 0.6D);
        map.put(Attributes.MOVEMENT_SPEED, 0.3D);
        map.put(Attributes.ATTACK_DAMAGE, 1d);
        map.put(Attributes.FOLLOW_RANGE, 48D);
        map.put(Attributes.ARMOR, 0d);
        map.put(Attributes.ARMOR_TOUGHNESS, 0d);
        map.put(Attributes.ATTACK_KNOCKBACK, 0d);
    });

    public static final CombatData DEFAULT = new CombatData(false, true, true, false, DEFAULT_ATTRIBUTES);

    /**
     * A {@link Codec<CombatData>} that can be parsed to create a
     * {@link CombatData} object.
     */
    public static final Codec<CombatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isPassive").orElse(false).forGetter(CombatData::isPassive),
            Codec.BOOL.fieldOf("removeStingerOnAttack").orElse(true).forGetter(CombatData::removeStingerOnAttack),
            Codec.BOOL.fieldOf("inflictsPoison").orElse(true).forGetter(CombatData::inflictsPoison),
            Codec.BOOL.fieldOf("isInvulnerable").orElse(true).forGetter(CombatData::isInvulnerable),
            Codec.unboundedMap(Registry.ATTRIBUTE.byNameCodec(), Codec.DOUBLE).fieldOf("attributes").orElse(DEFAULT_ATTRIBUTES).forGetter(CombatData::attributes)
    ).apply(instance, CombatData::new));
}
