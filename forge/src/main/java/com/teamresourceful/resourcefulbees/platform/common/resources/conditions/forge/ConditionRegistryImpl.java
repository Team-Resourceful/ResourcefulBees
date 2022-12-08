package com.teamresourceful.resourcefulbees.platform.common.resources.conditions.forge;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.Conditional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.ArrayList;
import java.util.List;

public class ConditionRegistryImpl {

    private static final List<ForgeConditionalSerializer> SERIALIZERS = new ArrayList<>();

    public static void registerCondition(Conditional condition) {
        SERIALIZERS.add(new ForgeConditionalSerializer(condition));
    }

    public static void freeze() {
        SERIALIZERS.forEach(CraftingHelper::register);
    }

    private record ForgeConditional(Conditional condition, boolean value) implements ICondition {

        @Override
        public ResourceLocation getID() {
            return condition.getId();
        }

        @Override
        public boolean test(IContext iContext) {
            return value;
        }
    }

    private record ForgeConditionalSerializer(Conditional conditional) implements IConditionSerializer<ForgeConditional> {

        @Override
        public void write(JsonObject json, ForgeConditional value) {}

        @Override
        public ForgeConditional read(JsonObject json) {
            return new ForgeConditional(conditional, conditional.test(json));
        }

        @Override
        public ResourceLocation getID() {
            return conditional.getId();
        }
    }
}
