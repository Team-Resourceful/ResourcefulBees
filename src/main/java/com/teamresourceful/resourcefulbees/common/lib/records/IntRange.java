package com.teamresourceful.resourcefulbees.common.lib.records;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public record IntRange(
        Holder<ContextIntProvider> value,
        int minInclusive,
        int maxInclusive
) implements ContextIntProvider {

    /**
     * Creates a codec for a ContextIntProvider constrained to the supplied range.
     *
     * The range is defined by the Java codec declaration rather than serialized.
     */
    public static Codec<Holder<ContextIntProvider>> codec(
            int minInclusive,
            int maxInclusive
    ) {
        return ContextIntProviders.CODEC.xmap(
                provider -> Holder.direct(
                        new IntRange(provider, minInclusive, maxInclusive)
                ),
                holder -> {
                    if (holder.value() instanceof IntRange range) {
                        return range.value();
                    }

                    return holder;
                }
        );
    }

    @Override
    public int getIntUnsafe(LootContext context) throws ArithmeticException {
        int result = this.value.value().getIntUnsafe(context);

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
    public void validate(ValidationContext context) {
        Validatable.validateHolder(context, "value", this.value);
    }

    @Override
    public MapCodec<IntRange> codec() {
        return MAP_CODEC;
    }

    /*
     * This codec is only needed to satisfy ContextIntProvider's serialization
     * contract if IntRange itself is ever serialized as a provider.
     */
    public static final MapCodec<IntRange> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ContextIntProviders.CODEC
                            .fieldOf("value")
                            .forGetter(IntRange::value),

                    Codec.INT
                            .fieldOf("min")
                            .forGetter(IntRange::minInclusive),

                    Codec.INT
                            .fieldOf("max")
                            .forGetter(IntRange::maxInclusive)
            ).apply(instance, IntRange::new));
}
