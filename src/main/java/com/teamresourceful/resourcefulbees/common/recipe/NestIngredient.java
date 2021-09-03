package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StackList;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestIngredient extends Ingredient {

    public static final List<ItemStack> NESTS = ModItems.NESTS_ITEMS.getEntries().stream()
            .filter(RegistryObject::isPresent).map(RegistryObject::get).map(Item::getDefaultInstance).collect(Collectors.toList());

    public int tier;

    protected NestIngredient(int tier) {
        super(Stream.of(new StackList(getNests(tier))));
        this.tier = tier;
    }

    @SuppressWarnings("ConstantConditions")
    public static Collection<ItemStack> getNests(int tier) {
        return NESTS.stream().map(stack ->  {
            stack = stack.copy();
            stack.setTag(new CompoundNBT());
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(NBTConstants.NBT_TIER, tier);
            stack.getTag().put(NBTConstants.NBT_BLOCK_ENTITY_TAG, tag);
            return stack;
        }).collect(Collectors.toList());
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
        if (!stack.hasTag()) return -1;
        CompoundNBT tag = stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
        if (tag == null) return -1;
        return tag.contains(NBTConstants.NBT_TIER) ? tag.getInt(NBTConstants.NBT_TIER) : -1;
    }

    public static class Serializer implements IIngredientSerializer<NestIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull NestIngredient parse(@NotNull PacketBuffer buffer) {
            return new NestIngredient(buffer.readInt());
        }

        @Override
        public @NotNull NestIngredient parse(@NotNull JsonObject json) {
            if (json.has("tier")) return new NestIngredient(json.get("tier").getAsInt());
            throw new JsonSyntaxException("Requires tier element for 'resourcefulbees:nest' ingredient!");
        }

        @Override
        public void write(@NotNull PacketBuffer buffer, @NotNull NestIngredient ingredient) {
            buffer.writeInt(ingredient.tier);
        }
    }
}
