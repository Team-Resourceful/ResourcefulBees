package com.teamresourceful.resourcefulbees.common.resources.conditions;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.Conditional;
import net.minecraft.resources.ResourceLocation;

public final class LoadDevRecipes implements Conditional {

    private static final ResourceLocation ID = new ModResourceLocation("dev_recipes");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(JsonObject jsonObject) {
        return GeneralConfig.enableDevBees;
    }
}
