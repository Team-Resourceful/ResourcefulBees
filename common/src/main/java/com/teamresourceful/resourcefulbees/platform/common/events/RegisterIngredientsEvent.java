package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredientSerializer;

public record RegisterIngredientsEvent(IngredientRegister register) {

    public static final EventHelper<RegisterIngredientsEvent> EVENT = new EventHelper<>();

    public <T extends CodecIngredient<T>> void register(CodecIngredientSerializer<T> serializer) {
        register.register(serializer);
    }

    @FunctionalInterface
    public interface IngredientRegister {
        <T extends CodecIngredient<T>> void register(CodecIngredientSerializer<T> serializer);
    }

}
