package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.forge;

import com.teamresourceful.resourcefulbees.platform.common.events.RegisterIngredientsEvent;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredientSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.HashMap;
import java.util.Map;

public final class ForgeIngredientHelper {

    private static final Map<ResourceLocation, ForgeIngredientSerializer<?>> SERIALIZERS = new HashMap<>();

    public static ForgeIngredientSerializer<?> get(ResourceLocation id) {
        return SERIALIZERS.get(id);
    }

    public static <T extends CodecIngredient<T>> void register(CodecIngredientSerializer<T> serializer) {
        SERIALIZERS.put(serializer.id(), new ForgeIngredientSerializer<>(serializer));
    }

    public static void init() {
        RegisterIngredientsEvent event = new RegisterIngredientsEvent(ForgeIngredientHelper::register);
        RegisterIngredientsEvent.EVENT.fire(event);
        SERIALIZERS.forEach(CraftingHelper::register);
    }
}
