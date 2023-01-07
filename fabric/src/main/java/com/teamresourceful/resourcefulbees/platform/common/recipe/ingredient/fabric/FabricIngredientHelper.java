package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.fabric;

import com.teamresourceful.resourcefulbees.platform.common.events.RegisterIngredientsEvent;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredientSerializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class FabricIngredientHelper {

    private static final Map<ResourceLocation, FabricIngredientSerializer<?>> SERIALIZERS = new HashMap<>();

    public static FabricIngredientSerializer<?> get(ResourceLocation id) {
        return SERIALIZERS.get(id);
    }

    public static <T extends CodecIngredient<T>> void register(CodecIngredientSerializer<T> serializer) {
        SERIALIZERS.put(serializer.id(), new FabricIngredientSerializer<>(serializer));
    }

    public static void init() {
        RegisterIngredientsEvent event = new RegisterIngredientsEvent(FabricIngredientHelper::register);
        RegisterIngredientsEvent.EVENT.fire(event);
        SERIALIZERS.values().forEach(CustomIngredientSerializer::register);
    }
}
