package com.teamresourceful.resourcefulbees.common.recipe.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public class AmountSensitiveIngredient extends Ingredient implements IAmountSensitive {

    public static final AmountSensitiveIngredient EMPTY = new AmountSensitiveIngredient(Stream.empty(), 0);

    private final int count;

    protected AmountSensitiveIngredient(Stream<? extends Value> value, int count) {
        super(value);
        this.count = count;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && stack.getCount() >= count && super.test(stack);
    }

    @Override
    public int getAmount() {
        return count;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull JsonElement toJson() {
        if (super.toJson() instanceof JsonObject json) {
            json.addProperty("type", CraftingHelper.getID(getSerializer()).toString());
            json.addProperty("count", count);
            return json;
        }
        throw new IllegalStateException("Ingredient was not single.");
    }

    public static class Serializer implements IIngredientSerializer<AmountSensitiveIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull AmountSensitiveIngredient parse(@NotNull FriendlyByteBuf buffer) {
            int size = buffer.readInt();
            Stream<ItemValue> stream = Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(size);
            AmountSensitiveIngredient ingredient = new AmountSensitiveIngredient(stream, buffer.readInt());
            return ingredient.isEmpty() ? EMPTY : ingredient;
        }

        @Override
        public @NotNull AmountSensitiveIngredient parse(@NotNull JsonObject json) {
            AmountSensitiveIngredient ingredient = new AmountSensitiveIngredient(Stream.of(valueFromJson(json)), GsonHelper.getAsInt(json, "count", 1));
            return ingredient.isEmpty() ? EMPTY : ingredient;
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull AmountSensitiveIngredient ingredient) {
            buffer.writeCollection(Arrays.asList(ingredient.getItems()), FriendlyByteBuf::writeItem);
            buffer.writeInt(ingredient.getAmount());
        }
    }
}
