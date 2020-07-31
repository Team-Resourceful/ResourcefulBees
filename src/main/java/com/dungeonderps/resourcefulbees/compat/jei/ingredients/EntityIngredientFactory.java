package com.dungeonderps.resourcefulbees.compat.jei.ingredients;

import java.util.ArrayList;
import java.util.List;

import com.dungeonderps.resourcefulbees.config.BeeInfo;

public final class EntityIngredientFactory {
    private EntityIngredientFactory() {
    }

    public static List<EntityIngredient> create() {
        List<EntityIngredient> list = new ArrayList<>();
        BeeInfo.BEE_INFO.forEach((s, beeData) -> list.add(new EntityIngredient(beeData.getName(), 45.0f)));
        return list;
    }
}
