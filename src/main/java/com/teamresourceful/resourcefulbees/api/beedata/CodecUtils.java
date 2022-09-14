package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class CodecUtils {

    private CodecUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    //region FluidStack Codec
    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").orElse(1000).forGetter(FluidStack::getAmount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, CodecUtils::createFluidStack));

    private static FluidStack createFluidStack(Fluid fluid, int amount, Optional<CompoundTag> tagOptional) {
        return new FluidStack(fluid, amount, tagOptional.orElse(null));
    }
    //endregion

    public static final Codec<InclusiveRange<Integer>> Y_LEVEL = codec(Codec.INT, -512, 512);

    public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> p_184575_, T min, T max) {
        Function<InclusiveRange<T>, DataResult<InclusiveRange<T>>> function = (range) -> {
            if (range.minInclusive().compareTo(min) < 0) {
                return DataResult.error(String.format("Range limit too low, expected at least %s [%s-%s]", min, range.minInclusive(), range.maxInclusive()));
            } else if (range.maxInclusive().compareTo(max) > 0) {
                return DataResult.error(String.format("Range limit too high, expected at most %s [%s-%s]", max, range.minInclusive(), range.maxInclusive()));
            }
            return DataResult.success(range);
        };
        return codec(p_184575_).flatXmap(function, function);
    }

    public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> p_184573_) {
        return ExtraCodecs.intervalCodec(p_184573_, "min", "max", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
    }

    public static final Codec<Map<ApiaryTier, ItemStack>> APIARY_VARIATIONS = Codec.unboundedMap(ApiaryTier.CODEC, ItemStackCodec.CODEC);
    public static final Codec<Map<BeehiveTier, ItemStack>> BEEHIVE_VARIATIONS = Codec.unboundedMap(BeehiveTier.CODEC, ItemStackCodec.CODEC);

    public static Consumer<String> debugLog() {
        if (Boolean.FALSE.equals(CommonConfig.SHOW_DEBUG_INFO.get())) return s -> {};
        return ResourcefulBees.LOGGER::warn;
    }

    public static LazyHolder<Item> itemHolder(String id) {
        return new LazyHolder<>(Registry.ITEM, new ResourceLocation(id));
    }

    public static LazyHolder<Block> blockHolder(String id) {
        return new LazyHolder<>(Registry.BLOCK, new ResourceLocation(id));
    }

    public static LazyHolder<Fluid> fluidHolder(String id) {
        return new LazyHolder<>(Registry.FLUID, new ResourceLocation(id));
    }
}
