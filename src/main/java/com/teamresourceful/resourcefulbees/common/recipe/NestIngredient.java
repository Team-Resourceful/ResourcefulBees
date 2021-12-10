package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.MultiItemValue;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestIngredient extends Ingredient {

    private static final List<ItemStack> NESTS = ModItems.NESTS_ITEMS.getEntries().stream()
            .filter(RegistryObject::isPresent).map(RegistryObject::get).map(Item::getDefaultInstance).toList();

    private final int tier;

    protected NestIngredient(int tier) {
        super(Stream.of(new MultiItemValue(getNests(tier))));
        this.tier = tier;
    }

    public static NestIngredient ofTier(int tier) {
        return new NestIngredient(tier);
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "resourcefulbees:nest");
        json.addProperty("tier", tier);
        return json;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @SuppressWarnings("ConstantConditions")
    public static Collection<ItemStack> getNests(int tier) {
        return NESTS.stream().map(stack ->  {
            stack = stack.copy();
            stack.setTag(new CompoundTag());
            CompoundTag tag = new CompoundTag();
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
        CompoundTag tag = stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
        if (tag == null) return -1;
        return tag.contains(NBTConstants.NBT_TIER) ? tag.getInt(NBTConstants.NBT_TIER) : -1;
    }

    public static class Serializer implements IIngredientSerializer<NestIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull NestIngredient parse(@NotNull FriendlyByteBuf buffer) {
            return new NestIngredient(buffer.readInt());
        }

        @Override
        public @NotNull NestIngredient parse(@NotNull JsonObject json) {
            if (json.has("tier")) return new NestIngredient(json.get("tier").getAsInt());
            throw new JsonSyntaxException("Requires tier element for 'resourcefulbees:nest' ingredient!");
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull NestIngredient ingredient) {
            buffer.writeInt(ingredient.tier);
        }
    }
}
