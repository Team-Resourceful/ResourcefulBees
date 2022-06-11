package com.teamresourceful.resourcefulbees.common.recipe.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.NBTIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTAmountSensitiveIngredient extends NBTIngredient implements IAmountSensitive {

    private final ItemStack stack;

    public NBTAmountSensitiveIngredient(ItemStack stack) {
        super(stack);
        this.stack = stack;
    }

    @Override
    public int getAmount() {
        return stack.getCount();
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        return input != null && input.getCount() >= stack.getCount() && super.test(input);
    }

    @Override
    public @NotNull JsonElement toJson() {
        if (super.toJson() instanceof JsonObject json) {
            json.addProperty("type", CraftingHelper.getID(getSerializer()).toString());
            return json;
        }
        return super.toJson();
    }

    public static class Serializer implements IIngredientSerializer<NBTAmountSensitiveIngredient> {
        public static final NBTAmountSensitiveIngredient.Serializer INSTANCE = new NBTAmountSensitiveIngredient.Serializer();

        @Override
        public @NotNull NBTAmountSensitiveIngredient parse(FriendlyByteBuf buffer) {
            return new NBTAmountSensitiveIngredient(buffer.readItem());
        }

        @Override
        public @NotNull NBTAmountSensitiveIngredient parse(@NotNull JsonObject json) {
            return new NBTAmountSensitiveIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(FriendlyByteBuf buffer, NBTAmountSensitiveIngredient ingredient) {
            buffer.writeItem(ingredient.stack);
        }
    }
}
