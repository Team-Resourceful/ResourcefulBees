package com.teamresourceful.resourcefulbees.api.beedata.itemsholder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.teamresourceful.resourcefulbees.common.ingredients.ItemHolderIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;
import java.util.Set;

public record ItemHolder(Set<Item> items) implements IItemHolder {

    public ItemHolder(Item... item) {
        this(Set.of(item));
    }

    @Override
    public Set<Item> getItems() {
        return items;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return items.contains(stack.getItem());
    }

    @Override
    public Ingredient getIngredient(int count) {
        return new ItemHolderIngredient(this, count);
    }

    @Override
    public JsonElement toJson() {
        return items.stream().map(Item::getRegistryName).filter(Objects::nonNull).map(ResourceLocation::toString).collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }
}
