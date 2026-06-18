package com.teamresourceful.resourcefulbees.common.resources.conditions;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.data.conditions.Conditional;
import net.minecraft.resources.Identifier;

public final class LoadDevRecipes implements Conditional {

    private static final Identifier ID = ModIdentifier.of("dev_recipes");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean test(JsonObject jsonObject) {
        return GeneralConfig.enableDevBees;
    }
}
