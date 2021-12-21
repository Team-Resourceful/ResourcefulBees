package com.teamresourceful.resourcefulbees.api.beedata.itemsholder;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.teamresourceful.resourcefulbees.common.ingredients.ItemHolderIngredient;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Set;

public record TagItemHolder(Tag<Item> tag) implements IItemHolder {

    @Override
    public Set<Item> getItems() {
        return Set.copyOf(tag.getValues());
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.is(tag);
    }

    @Override
    public Ingredient getIngredient(int count) {
        return new ItemHolderIngredient(this, count);
    }

    @Override
    public JsonElement toJson() {
        return tag instanceof Tag.Named<Item> named ? new JsonPrimitive(named.getName().toString()) : JsonNull.INSTANCE;
    }

}
