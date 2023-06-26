package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

public record DamageEffect(ResourceKey<DamageType> type, boolean hasEntity, int strength) {

    private static final String[] TENS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] UNITS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    public static final DamageEffect DEFAULT = new DamageEffect(DamageTypes.GENERIC, false, 0);

    public static final Codec<DamageEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("source").orElse(DamageTypes.GENERIC).forGetter(DamageEffect::type),
        Codec.BOOL.fieldOf("hasEntity").orElse(false).forGetter(DamageEffect::hasEntity),
        Codec.intRange(0, 20).fieldOf("strength").orElse(0).forGetter(DamageEffect::strength)
    ).apply(instance, DamageEffect::new));

    public DamageSource getDamageSource(Level level, @Nullable LivingEntity livingEntity) {
        if (hasEntity()) {
            return level.damageSources().source(this.type(), livingEntity);
        }
        return level.damageSources().source(this.type());
    }

    public Component getDisplayName() {
        return Component.literal(
            String.format("%s %s",
                WordUtils.capitalizeFully(type.location().getPath().replace("_", " ")),
                TENS[strength % 100 / 10] + UNITS[strength % 10]
            )
        );
    }
}
