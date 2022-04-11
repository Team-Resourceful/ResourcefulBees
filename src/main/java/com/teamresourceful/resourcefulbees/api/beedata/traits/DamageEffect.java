package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.enums.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record DamageEffect(DamageTypes source, int strength) {

    public static final DamageEffect DEFAULT = new DamageEffect(DamageTypes.GENERIC, 0);

    public static final Codec<DamageEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DamageTypes.CODEC.fieldOf("source").orElse(DamageTypes.GENERIC).forGetter(DamageEffect::source),
            Codec.intRange(0, 20).fieldOf("strength").orElse(0).forGetter(DamageEffect::strength)
    ).apply(instance, DamageEffect::new));

    public DamageSource getDamageSource(@Nullable LivingEntity livingEntity) {
        return source().getSource(livingEntity);
    }
}
