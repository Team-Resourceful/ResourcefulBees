package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;

import java.util.ArrayList;
import java.util.List;

public final class EntityIngredientFactory {
    private EntityIngredientFactory() {
    }

    public static List<EntityIngredient> create() {
        List<EntityIngredient> list = new ArrayList<>();
        BeeRegistry.getRegistry().getBees().forEach((s, beeData) -> list.add(new EntityIngredient(beeData.getEntityType(), -45.0f)));
        return list;
    }
}
