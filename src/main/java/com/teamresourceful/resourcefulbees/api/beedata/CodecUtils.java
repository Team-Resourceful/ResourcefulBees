package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CodecUtils {

    private CodecUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    //region ItemStack Codec
    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, CodecUtils::createItemStack));

    private static ItemStack createItemStack(ItemLike item, int count, Optional<CompoundTag> tagOptional) {
        return new ItemStack(item, count, tagOptional.orElse(null));
    }
    //endregion

    //region Ingredient Codec
    public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(CodecUtils::decodeIngredient, CodecUtils::encodeIngredient);

    private static DataResult<Ingredient> decodeIngredient(Dynamic<?> dynamic) {
        Object object = dynamic.convert(JsonOps.INSTANCE).getValue();
        if (object instanceof JsonElement jsonElement) {
            return DataResult.success(Ingredient.fromJson(jsonElement));
        } else {
            return DataResult.error("value was some how not a JsonElement");
        }
    }

    //TODO test if swapping INSTANCE for COMPRESSED and removing convert would also work
    private static Dynamic<JsonElement> encodeIngredient(Ingredient ingredient) {
        return new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()).convert(JsonOps.COMPRESSED);
    }
    //endregion

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

    public static final Codec<InclusiveRange<Integer>> Y_LEVEL = codec(Codec.INT, 0, 256);
    public static final Codec<InclusiveRange<Integer>> SPAWN_GROUP = codec(Codec.INT, 0, 8);

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

    public static final Codec<Map<ApiaryTier, ItemStack>> APIARY_VARIATIONS = Codec.unboundedMap(ApiaryTier.CODEC, ITEM_STACK_CODEC);
    public static final Codec<Map<BeehiveTier, ItemStack>> BEEHIVE_VARIATIONS = Codec.unboundedMap(BeehiveTier.CODEC, ITEM_STACK_CODEC);


    public static <A> Codec<Set<A>> createSetCodec(Codec<A> codec) {
        return codec.listOf().xmap(HashSet::new, ArrayList::new);
    }

    public static <A> Codec<Set<A>> createLinkedSetCodec(Codec<A> codec) {
        return codec.listOf().xmap(LinkedHashSet::new, LinkedList::new);
    }

    public static <T> Codec<T> passthrough(Function<T, JsonElement> encoder, Function<JsonElement, T> decoder) {
        return Codec.PASSTHROUGH.comapFlatMap(dynamic -> decoder(dynamic, decoder), item -> encoder(item, encoder));
    }

    private static <T> DataResult<T> decoder(Dynamic<?> dynamic, Function<JsonElement, T> decoder) {
        if (dynamic.getValue() instanceof JsonElement jsonElement) {
            return DataResult.success(decoder.apply(jsonElement));
        } else {
            return DataResult.error("value was some how not a JsonElement");
        }
    }

    private static <T> Dynamic<JsonElement> encoder(T input, Function<T, JsonElement> encoder) {
        return new Dynamic<>(JsonOps.INSTANCE, encoder.apply(input));
    }
}
