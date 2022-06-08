package com.teamresourceful.resourcefulbees.common.recipe.conditions;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class LoadDevRecipes implements ICondition {

    public static final LoadDevRecipes INSTANCE = new LoadDevRecipes();
    private static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "dev_recipes");

    private LoadDevRecipes() {
        //We only need 1 instance!
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return Boolean.TRUE.equals(CommonConfig.GENERATE_DEFAULT_RECIPES.get());
    }

    public static class Serializer implements IConditionSerializer<LoadDevRecipes> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, LoadDevRecipes value) {}

        @Override
        public LoadDevRecipes read(JsonObject json) {
            return LoadDevRecipes.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return LoadDevRecipes.ID;
        }
    }
}
