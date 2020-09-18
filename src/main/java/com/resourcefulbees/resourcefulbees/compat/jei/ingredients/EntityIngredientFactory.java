package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;

import java.util.ArrayList;
import java.util.List;

public final class EntityIngredientFactory {
    private EntityIngredientFactory() {
    }

    public static List<EntityIngredient> create() {
        List<EntityIngredient> list = new ArrayList<>();
        BeeRegistry.getRegistry().getBees().forEach((s, beeData) -> list.add(new EntityIngredient(beeData.getName(), 45.0f)));
        //list.add(new EntityIngredient(BeeConstants.VANILLA_BEE_TYPE, 45.0f));
        return list;
    }
}
