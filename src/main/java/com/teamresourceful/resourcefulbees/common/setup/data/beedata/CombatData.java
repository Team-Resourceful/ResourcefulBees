package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;

public record CombatData(
        boolean isPassive, boolean removeStingerOnAttack,
        boolean inflictsPoison, boolean isInvulnerable,
        Map<Attribute, Double> attributes
) implements BeeCombatData {

    private static final Map<Attribute, Double> DEFAULT_ATTRIBUTES = Util.make(new HashMap<>(), map -> {
        map.put(Attributes.MAX_HEALTH, 10d);
        map.put(Attributes.FLYING_SPEED, 0.6D);
        map.put(Attributes.MOVEMENT_SPEED, 0.3D);
        map.put(Attributes.ATTACK_DAMAGE, 1d);
        map.put(Attributes.FOLLOW_RANGE, 48D);
        map.put(Attributes.ARMOR, 0d);
        map.put(Attributes.ARMOR_TOUGHNESS, 0d);
        map.put(Attributes.ATTACK_KNOCKBACK, 0d);
    });
    private static final BeeCombatData DEFAULT = new CombatData(false, true, true, false, DEFAULT_ATTRIBUTES);
    private static final Codec<BeeCombatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("isPassive", false).forGetter(BeeCombatData::isPassive),
            Codec.BOOL.optionalFieldOf("removeStingerOnAttack", true).forGetter(BeeCombatData::removeStingerOnAttack),
            Codec.BOOL.optionalFieldOf("inflictsPoison", true).forGetter(BeeCombatData::inflictsPoison),
            Codec.BOOL.optionalFieldOf("isInvulnerable", false).forGetter(BeeCombatData::isInvulnerable),
            Codec.unboundedMap(BuiltInRegistries.ATTRIBUTE.byNameCodec(), Codec.DOUBLE).optionalFieldOf("attributes", DEFAULT_ATTRIBUTES).forGetter(BeeCombatData::attributes)
    ).apply(instance, CombatData::new));
    public static final BeeDataSerializer<BeeCombatData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("combat"), 1, id -> CODEC, DEFAULT);

    @Override
    public AttributeSupplier.Builder buildAttributes(AttributeSupplier.Builder builder) {
        DEFAULT_ATTRIBUTES.forEach(builder::add);
        attributes.forEach(builder::add);
        return builder;
    }

    @Override
    public BeeDataSerializer<BeeCombatData> serializer() {
        return SERIALIZER;
    }
}
