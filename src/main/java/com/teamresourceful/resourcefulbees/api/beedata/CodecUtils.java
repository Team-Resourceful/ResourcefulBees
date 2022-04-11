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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

@SuppressWarnings("unused")
public class CodecUtils {

    private CodecUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    //Codec for getting an ItemStack
    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, CodecUtils::createItemStack));

    //Codec for converting an ItemStack to an Ingredient
    public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(CodecUtils::decodeIngredient, CodecUtils::encodeIngredient);

    //Codec for getting a FluidStack
    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").orElse(1000).forGetter(FluidStack::getAmount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, CodecUtils::createFluidStack));

    public static final Codec<Map<ApiaryTier, ItemStack>> APIARY_VARIATIONS = Codec.unboundedMap(ApiaryTier.CODEC, ITEM_STACK_CODEC);
    public static final Codec<Map<BeehiveTier, ItemStack>> BEEHIVE_VARIATIONS = Codec.unboundedMap(BeehiveTier.CODEC, ITEM_STACK_CODEC);

    //helper method to create an item stack with an optional tag
    private static ItemStack createItemStack(ItemLike item, int count, Optional<CompoundTag> tagOptional) {
        return new ItemStack(item, count, tagOptional.orElse(null));
    }

    //helper method to create a fluid stack with an optional tag
    private static FluidStack createFluidStack(Fluid fluid, int amount, Optional<CompoundTag> tagOptional) {
        return new FluidStack(fluid, amount, tagOptional.orElse(null));
    }

    private static DataResult<Ingredient> decodeIngredient(Dynamic<?> dynamic) {
        Object object = dynamic.getValue();
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

    public static <A> Codec<Set<A>> createSetCodec(Codec<A> codec) {
        return codec.listOf().xmap(HashSet::new, ArrayList::new);
    }

    public static <A> Codec<Set<A>> createLinkedSetCodec(Codec<A> codec) {
        return codec.listOf().xmap(LinkedHashSet::new, LinkedList::new);
    }

    @SafeVarargs
    public static <E> Set<E> newLinkedHashSet(E... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }
}
