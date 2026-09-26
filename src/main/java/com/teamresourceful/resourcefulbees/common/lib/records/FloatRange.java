package com.teamresourceful.resourcefulbees.common.lib.records;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import org.jspecify.annotations.NonNull;

public record FloatRange(
        Holder<ContextFloatProvider> value,
        float minInclusive,
        float maxInclusive
) implements ContextFloatProvider {

    /**
     * Creates a codec for a ContextIntProvider constrained to the supplied range.
     *
     * The range is defined by the Java codec declaration rather than serialized.
     */
    public static Codec<Holder<ContextFloatProvider>> codec(float minInclusive, float maxInclusive) {
        return ContextFloatProviders.CODEC.xmap(
                provider -> Holder.direct(new FloatRange(provider, minInclusive, maxInclusive)),
                holder -> {
                    if (holder.value() instanceof FloatRange range) {
                        return range.value();
                    }

                    return holder;
                }
        );
    }

    @Override
    public float getFloatUnsafe(@NonNull LootContext context) throws ArithmeticException {
        float result = this.value.value().getFloatUnsafe(context);

        if (result < this.minInclusive || result > this.maxInclusive) {
            throw new ArithmeticException(
                    "Value " + result + " outside of range [" +
                            this.minInclusive + ":" +
                            this.maxInclusive + "]"
            );
        }

        return result;
    }

    @Override
    public void validate(@NonNull ValidationContext context) {
        Validatable.validateHolder(context, "value", this.value);
    }

    @Override
    public @NonNull MapCodec<FloatRange> codec() {
        return MAP_CODEC;
    }

    /*
     * This codec is only needed to satisfy ContextIntProvider's serialization
     * contract if IntRange itself is ever serialized as a provider.
     */
    public static final MapCodec<FloatRange> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ContextFloatProviders.CODEC
                            .fieldOf("value")
                            .forGetter(FloatRange::value),

                    Codec.FLOAT
                            .fieldOf("min")
                            .forGetter(FloatRange::minInclusive),

                    Codec.FLOAT
                            .fieldOf("max")
                            .forGetter(FloatRange::maxInclusive)
            ).apply(instance, FloatRange::new));
}
