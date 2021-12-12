package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StackList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HiveIngredient extends Ingredient {

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final List<ItemStack> NESTS = Arrays.asList(
            ModItems.OAK_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.ACACIA_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.GRASS_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.JUNGLE_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.NETHER_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.PRISMARINE_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.PURPUR_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.BIRCH_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.WITHER_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.BROWN_MUSHROOM_NEST_ITEM.get().getDefaultInstance(),
            ModItems.CRIMSON_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.CRIMSON_NYLIUM_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.DARK_OAK_NEST_ITEM.get().getDefaultInstance(),
            ModItems.RED_MUSHROOM_NEST_ITEM.get().getDefaultInstance(),
            ModItems.SPRUCE_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.WARPED_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.WARPED_NYLIUM_BEE_NEST_ITEM.get().getDefaultInstance(),
            ModItems.T1_BEEHIVE_ITEM.get().getDefaultInstance(),
            ModItems.T2_BEEHIVE_ITEM.get().getDefaultInstance(),
            ModItems.T3_BEEHIVE_ITEM.get().getDefaultInstance(),
            ModItems.T4_BEEHIVE_ITEM.get().getDefaultInstance()
    );

    private static final Map<Item, Integer> ITEM_TIER = Util.make(() -> {
        Map<Item, Integer> map = new HashMap<>();
        map.put(ModItems.T1_BEEHIVE_ITEM.get(), 1);
        map.put(ModItems.T2_BEEHIVE_ITEM.get(), 2);
        map.put(ModItems.T3_BEEHIVE_ITEM.get(), 3);
        map.put(ModItems.T4_BEEHIVE_ITEM.get(), 4);
        return map;
    });

    public int tier;


    protected HiveIngredient(int tier) {
        super(Stream.of(new StackList(getNests(tier))));
        this.tier = tier;
    }

    @SuppressWarnings("ConstantConditions")
    public static Collection<ItemStack> getNests(int tier) {

        List<ItemStack> nests = new ArrayList<>(ITEM_TIER.entrySet().stream().filter(i -> i.getValue() == tier).map(i -> new ItemStack(i.getKey())).collect(Collectors.toList()));

        NESTS.stream().filter(stack -> {
            if (ITEM_TIER.containsKey(stack.getItem())) {
                return ITEM_TIER.get(stack.getItem()) <= tier;
            }
            return true;
        }).map(stack -> {
            if (tier == 0) return stack;
            stack = stack.copy();
            stack.setTag(new CompoundNBT());
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(NBTConstants.NBT_TIER, tier);
            stack.getTag().put(NBTConstants.NBT_BLOCK_ENTITY_TAG, tag);
            return stack;
        }).forEach(nests::add);
        return nests;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        for (ItemStack nest : NESTS) {
            if (nest.getItem().equals(stack.getItem())) {
                return tier == getTier(stack);
            }
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private static int getTier(ItemStack stack) {
        if (stack.getTag() == null || !stack.getTag().contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) {
            return ITEM_TIER.get(stack.getItem()) == null ? -1 : ITEM_TIER.get(stack.getItem());
        }
        CompoundNBT tag = stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
        return tag.contains(NBTConstants.NBT_TIER) ? tag.getInt(NBTConstants.NBT_TIER) : -1;
    }

    public static class Serializer implements IIngredientSerializer<HiveIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull HiveIngredient parse(@NotNull PacketBuffer buffer) {
            return new HiveIngredient(buffer.readInt());
        }

        @Override
        public @NotNull HiveIngredient parse(@NotNull JsonObject json) {
            if (json.has("tier")) return new HiveIngredient(json.get("tier").getAsInt());
            throw new JsonSyntaxException("Requires tier element for 'resourcefulbees:hive' ingredient!");
        }

        @Override
        public void write(@NotNull PacketBuffer buffer, @NotNull HiveIngredient ingredient) {
            buffer.writeInt(ingredient.tier);
        }
    }
}
