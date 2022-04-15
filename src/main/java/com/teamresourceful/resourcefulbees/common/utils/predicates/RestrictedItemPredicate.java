package com.teamresourceful.resourcefulbees.common.utils.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.NbtPredicateAccessor;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record RestrictedItemPredicate(Item item, NbtPredicate nbt, MinMaxBounds.Ints durability, MinMaxBounds.Ints count) {

    public static final Codec<RestrictedItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().fieldOf("id").forGetter(RestrictedItemPredicate::item),
            CodecUtils.passthrough(NbtPredicate::serializeToJson, NbtPredicate::fromJson).fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(RestrictedItemPredicate::nbt),
            CodecUtils.passthrough(MinMaxBounds.Ints::serializeToJson, MinMaxBounds.Ints::fromJson).fieldOf("durability").orElse(MinMaxBounds.Ints.ANY).forGetter(RestrictedItemPredicate::durability),
            CodecUtils.passthrough(MinMaxBounds.Ints::serializeToJson, MinMaxBounds.Ints::fromJson).fieldOf("count").orElse(MinMaxBounds.Ints.ANY).forGetter(RestrictedItemPredicate::count)
    ).apply(instance, RestrictedItemPredicate::new));

    public Optional<CompoundTag> getTag() {
        if (nbt() == null) return Optional.empty();
        return Optional.ofNullable(((NbtPredicateAccessor)nbt).getTag());
    }

    public boolean matches(ItemStack stack) {
        if (item == null) {
            return false;
        } else if (stack.is(this.item)) {
            return false;
        } else if (!this.durability.isAny() && !stack.isDamageableItem()) {
            return false;
        } else if (!this.durability.matches(stack.getMaxDamage() - stack.getDamageValue())) {
            return false;
        } else if (!this.count.matches(stack.getCount())) {
            return false;
        } else {
            return this.nbt.matches(stack);
        }
    }
}
