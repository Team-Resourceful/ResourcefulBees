package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.fabric.FabricIngredientHelper;
import net.fabricmc.api.ModInitializer;

public class ResourcefulBees implements ModInitializer {
    @Override
    public void onInitialize() {
        ModConstants.forceInit();
        FabricIngredientHelper.init();
    }
}
