package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.resourcefulbees.resourcefulbees.config.BeeInfo;

import java.util.ArrayList;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.DEFAULT_BEE_TYPE;

public final class EntityIngredientFactory {
    private EntityIngredientFactory() {
    }

    public static List<EntityIngredient> create() {
        List<EntityIngredient> list = new ArrayList<>();
        BeeInfo.getBees().forEach((s, beeData) -> {
            if (!s.equals(DEFAULT_BEE_TYPE))
                list.add(new EntityIngredient(beeData.getName(), 45.0f));
        });
        return list;
    }
}
