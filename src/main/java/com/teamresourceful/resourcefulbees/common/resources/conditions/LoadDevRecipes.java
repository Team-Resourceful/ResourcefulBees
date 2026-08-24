package com.teamresourceful.resourcefulbees.common.resources.conditions;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.NonNull;

public final class LoadDevRecipes implements ICondition {

    public static final LoadDevRecipes INSTANCE = new LoadDevRecipes();

    public static final MapCodec<LoadDevRecipes> CODEC =
            MapCodec.unit(INSTANCE);

    private LoadDevRecipes() {
    }

    @Override
    public boolean test(@NonNull IContext context) {
        return GeneralConfig.enableDevBees;
    }

    @Override
    public @NonNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
