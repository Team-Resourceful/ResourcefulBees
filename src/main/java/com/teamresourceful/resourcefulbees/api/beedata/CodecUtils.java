package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CodecUtils {

    private CodecUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    // Codecs for getting a Set of Items via a SINGLE tag or a List of Items
    public static final Codec<Set<Item>> SET_FROM_ITEM_LIST = Registry.ITEM.byNameCodec().listOf().xmap(HashSet::new, ArrayList::new);
    public static final Codec<Set<Item>> SET_FROM_ITEM_TAG = Codec.STRING.comapFlatMap(CodecUtils::convertItemTagToSet, CodecUtils::convertTagSetToString);
    public static final Codec<Set<Item>> ITEM_SET_CODEC = Codec.either(SET_FROM_ITEM_TAG, SET_FROM_ITEM_LIST).xmap(either -> either.map(list -> list, list -> list), Either::left);

    // Codecs for getting a Set of blocks via a SINGLE block or fluid tag or a List of Blocks
    public static final Codec<Set<Block>> SET_FROM_BLOCK_LIST = Registry.BLOCK.byNameCodec().listOf().xmap(HashSet::new, ArrayList::new);
    public static final Codec<Set<Block>> SET_FROM_BLOCK_TAG = Codec.STRING.comapFlatMap(CodecUtils::convertBlockTagToSet, CodecUtils::convertTagSetToString);
    public static final Codec<Set<Block>> BLOCK_SET_FROM_FLUID_TAG = Codec.STRING.comapFlatMap(CodecUtils::convertFluidTagToBlockSet, CodecUtils::convertTagSetToString);
    public static final Codec<Set<Block>> BLOCK_SET_CODEC = Codec.either(Codec.either(SET_FROM_BLOCK_TAG, BLOCK_SET_FROM_FLUID_TAG).xmap(either -> either.map(list -> list, list -> list), Either::left), SET_FROM_BLOCK_LIST).xmap(either -> either.map(list -> list, list -> list), Either::left);

    //Codec for getting an ItemStack
    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, CodecUtils::createItemStack)); //can't use this method in 1.17 due to constructor being removed!!!

    //Codec for converting an ItemStack to an Ingredient
    public static final Codec<Ingredient> INGREDIENT_CODEC = ITEM_STACK_CODEC.comapFlatMap(CodecUtils::convertToIngredient, CodecUtils::ingredientToItemStack);

    //Codec for getting a FluidStack
    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").orElse(1000).forGetter(FluidStack::getAmount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, CodecUtils::createFluidStack));


    private static DataResult<Set<Item>> convertItemTagToSet(String input) {
        Tag<Item> tag = BeeInfoUtils.getItemTag(input);
        return tag != null ? DataResult.success(new HashSet<>(tag.getValues())) : DataResult.success(new HashSet<>());
    }

    private static DataResult<Set<Block>> convertBlockTagToSet(String input) {
        Tag<Block> tag = BeeInfoUtils.getBlockTag(input);
        return tag != null ? DataResult.success(new HashSet<>(tag.getValues())) : DataResult.success(new HashSet<>());
    }

    private static DataResult<Set<Fluid>> convertFluidTagToSet(String input) {
        Tag<Fluid> tag = BeeInfoUtils.getFluidTag(input);
        return tag != null ? DataResult.success(new HashSet<>(tag.getValues())) : DataResult.success(new HashSet<>());
    }

    private static DataResult<Set<Block>> convertFluidTagToBlockSet(String input) {
        Tag<Fluid> tag = BeeInfoUtils.getFluidTag(input);
        return tag != null ? DataResult.success(tag.getValues().stream().map(fluid -> fluid.defaultFluidState().createLegacyBlock().getBlock()).collect(Collectors.toSet())) : DataResult.success(new HashSet<>());
    }

    private static String convertTagSetToString(Set<?> set) {
        return Tag.fromSet(set).toString();
    }

    //helper method to create an item stack with an optional tag
    private static ItemStack createItemStack(ItemLike item, int count, Optional<CompoundTag> tagOptional) {
        return new ItemStack(item, count, tagOptional.orElse(null));
    }

    //helper method to create a fluid stack with an optional tag
    private static FluidStack createFluidStack(Fluid fluid, int amount, Optional<CompoundTag> tagOptional) {
        return new FluidStack(fluid, amount, tagOptional.orElse(null));
    }

    private static DataResult<Ingredient> convertToIngredient(ItemStack stack) {
        return DataResult.success(new NBTIngredient(stack){});
    }

    private static ItemStack ingredientToItemStack(Ingredient ingredient) {
        return ingredient.getItems()[0];
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
