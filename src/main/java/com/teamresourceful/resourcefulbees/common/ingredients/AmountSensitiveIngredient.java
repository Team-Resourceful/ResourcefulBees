package com.teamresourceful.resourcefulbees.common.ingredients;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.NBTIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmountSensitiveIngredient extends NBTIngredient {

    private final ItemStack stack;

    public AmountSensitiveIngredient(ItemStack stack) {
        super(stack);
        this.stack = stack;
    }

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

    public static class Serializer extends NBTIngredient.Serializer {
        public static final AmountSensitiveIngredient.Serializer INSTANCE = new AmountSensitiveIngredient.Serializer();
    }
}
