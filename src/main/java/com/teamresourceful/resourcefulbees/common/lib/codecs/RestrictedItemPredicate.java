package com.teamresourceful.resourcefulbees.common.lib.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.util.CodecUtils;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RestrictedItemPredicate(
        @NotNull Item item,
        Optional<DataComponentPatch> components,
        MinMaxBounds.Ints durability,
        MinMaxBounds.Ints count
) {

    public static final Codec<RestrictedItemPredicate> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.ITEM
                            .byNameCodec()
                            .fieldOf("id")
                            .forGetter(RestrictedItemPredicate::item),

                    DataComponentPatch.CODEC
                            .optionalFieldOf("components")
                            .forGetter(RestrictedItemPredicate::components),

                    MinMaxBounds.Ints.CODEC
                            .fieldOf("durability")
                            .orElse(MinMaxBounds.Ints.ANY)
                            .forGetter(RestrictedItemPredicate::durability),

                    MinMaxBounds.Ints.CODEC
                            .fieldOf("count")
                            .orElse(MinMaxBounds.Ints.ANY)
                            .forGetter(RestrictedItemPredicate::count)
            ).apply(instance, RestrictedItemPredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictedItemPredicate> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()),
                    RestrictedItemPredicate::item,

                    ByteBufCodecs.optional(DataComponentPatch.STREAM_CODEC),
                    RestrictedItemPredicate::components,

                    MinMaxBounds.Ints.STREAM_CODEC,
                    RestrictedItemPredicate::durability,

                    MinMaxBounds.Ints.STREAM_CODEC,
                    RestrictedItemPredicate::count,

                    RestrictedItemPredicate::new
            );

    public Optional<DataComponentPatch> getComponents() {
        return this.components;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!stack.is(this.item)) {
            return false;
        }

        if (!this.durability.isAny()) {
            if (!stack.isDamageableItem()) {
                return false;
            }

            int remainingDurability =
                    stack.getMaxDamage() - stack.getDamageValue();

            if (!this.durability.matches(remainingDurability)) {
                return false;
            }
        }

        if (!this.count.matches(stack.getCount())) {
            return false;
        }

        return this.components
                .map(patch -> CodecUtils.matchesComponents(patch, stack.getComponents()))
                .orElse(true);
    }
}
