package com.teamresourceful.resourcefulbees.common.recipes.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * This is an immutable class that represents a fluid and amount, with optional nbt.
 * It is used in recipes to represent a fluid input or output.
 * @param fluid
 * @param amount
 * @param tag
 */
public record RecipeFluid(Fluid fluid, int amount, CompoundTag tag) {

    public static final RecipeFluid EMPTY = new RecipeFluid(Fluids.EMPTY, 0);
    public static final Codec<RecipeFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(RecipeFluid::fluid),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").orElse(1000).forGetter(RecipeFluid::amount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(CodecExtras.optionalFor(RecipeFluid::tag))
    ).apply(instance, (fluid, amount, tag) -> new RecipeFluid(fluid, amount, tag.orElse(null))));

    public RecipeFluid(Fluid fluid, int amount) {
        this(fluid, amount, null);
    }

    public RecipeFluid(Fluid fluid) {
        this(fluid, 1000);
    }

    public boolean matches(Predicate<RecipeFluid> tester) {
        return tester.test(this);
    }

    public boolean matches(Fluid fluid) {
        return this.fluid == fluid;
    }

    public boolean matches(Fluid fluid, int amount) {
        return matches(fluid) && this.amount == amount;
    }

    public boolean matches(Fluid fluid, int amount, CompoundTag tag) {
        return matches(fluid, amount) && Objects.equals(this.tag, tag);
    }

    public boolean matches(Fluid fluid, CompoundTag tag) {
        return matches(fluid) && Objects.equals(this.tag, tag);
    }

    public boolean matches(RecipeFluid other) {
        return matches(other.fluid(), other.amount(), other.tag());
    }

    public boolean isEmpty() {
        return fluid == null || fluid == Fluids.EMPTY || amount <= 0;
    }

    public FluidHolder toHolder() {
        return FluidHolder.of(fluid, amount, tag);
    }
}
