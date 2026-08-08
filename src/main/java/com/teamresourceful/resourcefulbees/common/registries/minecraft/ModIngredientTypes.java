package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipes.ingredients.BeeJarIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModIngredientTypes {

    private ModIngredientTypes() {
    }

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, ModConstants.MOD_ID);

    public static final Supplier<IngredientType<BeeJarIngredient>> BEE_JAR = INGREDIENT_TYPES.register("bee_jar", () -> new IngredientType<>(BeeJarIngredient.MAP_CODEC, BeeJarIngredient.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        INGREDIENT_TYPES.register(modBus);
    }
}
