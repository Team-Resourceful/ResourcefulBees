//package com.teamresourceful.resourcefulbees.platform.common.resources.conditions;
//
//import com.google.gson.JsonObject;
//import com.mojang.serialization.MapCodec;
//import net.minecraft.resources.Identifier;
//import net.neoforged.neoforge.common.conditions.ICondition;
//import org.jspecify.annotations.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ConditionRegistry {
//
//    private static final List<ForgeConditionalSerializer> SERIALIZERS = new ArrayList<>();
//
//    public static void registerCondition(Conditional condition) {
//        SERIALIZERS.add(new ForgeConditionalSerializer(condition));
//    }
//
//    public static void freeze() {
//        SERIALIZERS.forEach(CraftingHelper::register);
//    }
//
//    private record ForgeConditional(Conditional condition, boolean value) implements ICondition {
//
///*        @Override
//        public Identifier getID() {
//            return condition.getId();
//        }*/
//
//        @Override
//        public boolean test(@NonNull IContext iContext) {
//            return value;
//        }
//
//        @Override
//        public MapCodec<? extends ICondition> codec() {
//            return null;
//        }
//    }
//
//    private record ForgeConditionalSerializer(Conditional conditional) implements IConditionSerializer<ForgeConditional> {
//
//        @Override
//        public void write(JsonObject json, ForgeConditional value) {}
//
//        @Override
//        public ForgeConditional read(JsonObject json) {
//            return new ForgeConditional(conditional, conditional.test(json));
//        }
//
//        @Override
//        public Identifier getID() {
//            return conditional.getId();
//        }
//    }
//}
