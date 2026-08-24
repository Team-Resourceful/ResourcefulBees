package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.resources.conditions.LoadDevRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModConditions {

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ModConstants.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<LoadDevRecipes>> DEV_RECIPES = CONDITIONS.register("dev_recipes", () -> LoadDevRecipes.CODEC);

    private ModConditions() {
    }

    public static void init(IEventBus bus) {
        CONDITIONS.register(bus);
    }
}