package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.forge;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ForgeIngredient<T extends CodecIngredient<T>> extends AbstractIngredient {

    private final CodecIngredient<T> ingredient;
    @Nullable private ItemStack[] stacks;
    @Nullable private IntList stackingIds;

    protected ForgeIngredient(CodecIngredient<T> ingredient) {
        super(Stream.of());
        this.ingredient = ingredient;
    }

    public CodecIngredient<T> getIngredient() {
        return this.ingredient;
    }

    @Override
    public ItemStack @NotNull [] getItems() {
        if (this.stacks == null) this.stacks = this.ingredient.getStacksAsArray();
        return this.stacks;
    }

    @Override
    public boolean isSimple() {
        return this.ingredient.isConstant();
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return this.ingredient.test(stack);
    }

    @Override
    public boolean isEmpty() {
        return this.ingredient.isEmpty();
    }

    @Override
    public @NotNull IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.stackingIds = Ingredient.of(getItems()).getStackingIds();
        }
        return this.stackingIds;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return ForgeIngredientHelper.get(this.ingredient.serializer().id());
    }

    @Override
    public @NotNull JsonElement toJson() {
        return this.ingredient.serializer()
                .codec()
                .encodeStart(JsonOps.INSTANCE, this.ingredient)
                .getOrThrow(false, ModConstants.LOGGER::error);
    }


}
