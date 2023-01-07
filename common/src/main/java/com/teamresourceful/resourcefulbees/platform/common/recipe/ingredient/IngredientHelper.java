package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.crafting.Ingredient;

public final class IngredientHelper {

    private IngredientHelper() {
        throw new UtilityClassError();
    }

    @ExpectPlatform
    public static Ingredient getIngredient(CodecIngredient<?> ingredient) {
        throw new NotImplementedError();
    }
}
