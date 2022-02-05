package com.teamresourceful.resourcefulbees.api.beedata.itemsholder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface IItemHolder {

    Set<Item> getItems();

    boolean matches(ItemStack stack);

    default Ingredient getIngredient() {
        return getIngredient(1);
    }

    Ingredient getIngredient(int count);

    JsonElement toJson();

    static IItemHolder fromJson(JsonElement element) {
        if (element instanceof JsonArray array) {
            Set<String> strings = new HashSet<>();
            for (JsonElement jsonElement : array) {
                if (jsonElement instanceof JsonPrimitive primitive && primitive.isString()) {
                    if (!strings.add(primitive.getAsString())) {
                        throw new IllegalArgumentException("Duplicate values in list: " + primitive.getAsString());
                    }
                }else {
                    throw new IllegalArgumentException("Json array must be list of Strings. Got: " + element);
                }
            }
            return new ItemHolder(strings.stream()
                    .map(ResourceLocation::new)
                    .map(ForgeRegistries.ITEMS::getValue).collect(Collectors.toSet()));
        } else if (element instanceof JsonPrimitive primitive && primitive.isString()) {
            Tag<Item> tag = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, new ResourceLocation(primitive.getAsString()),
                    tagPassed -> new JsonSyntaxException("Unknown item tag '" + tagPassed + "'"));
            return new TagItemHolder(tag);
        } else {
            throw new IllegalArgumentException("Type is not a array or string, like a typo. Element provided: " + element);
        }
    }
}
