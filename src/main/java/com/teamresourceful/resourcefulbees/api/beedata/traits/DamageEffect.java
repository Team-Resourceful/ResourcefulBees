package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.utils.DamageUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record DamageEffect(String source, int strength) {

    public static final DamageEffect DEFAULT = new DamageEffect("generic", 0);

    public static final Codec<DamageEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("source").orElse("generic").forGetter(DamageEffect::source),
            Codec.intRange(0, 20).fieldOf("strength").orElse(0).forGetter(DamageEffect::strength)
    ).apply(instance, DamageEffect::new));

    public DamageSource getDamageSource(@Nullable LivingEntity livingEntity) {
        return DamageUtils.damageByName(source(), livingEntity);
    }

    public DamageSource getDamageSource() {
        return DamageUtils.damageByName(source());
    }
}
