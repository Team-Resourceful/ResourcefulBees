package com.teamresourceful.resourcefulbees.common.compat.productivebees;

import com.teamresourceful.resourcefulbees.common.compat.base.ModCompat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ProductiveBeesCompat implements ModCompat {

    private static final String HELPER_CLASS = "cy.jdkdigital.productivebees.util.BeeHelper";
    private static final String PRODUCE_METHOD = "getBeeProduce";

    private static final Method BEE_PRODUCE_METHOD = getBeeProduceMethod();

    private static Method getBeeProduceMethod() {
        try {
            Class<?> helperClass = Class.forName(HELPER_CLASS);
            Method method = helperClass.getMethod(PRODUCE_METHOD,
                    Level.class, // level
                    Bee.class,  // beeEntity
                    boolean.class  // hasCombBlockUpgrade
            );
            if (!Modifier.isStatic(method.getModifiers())) return null;
            if (!Modifier.isPublic(method.getModifiers())) return null;
            return method;
        } catch (Exception e) {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public static List<ItemStack> getBeeProduce(Level level, Bee bee, boolean hasCombBlockUpgrade) {
        if (BEE_PRODUCE_METHOD != null) {
            try {
                List<ItemStack> newStacks = new ArrayList<>();
                for (ItemStack stack : (List<ItemStack>) BEE_PRODUCE_METHOD.invoke(null, level, bee, hasCombBlockUpgrade)) {
                    for (ItemStack newStack : newStacks) {
                        if (ItemStack.isSameItemSameTags(newStack, stack)) {
                            newStack.grow(stack.getCount());
                            break;
                        }
                    }
                    newStacks.add(stack.copy());
                }
                return newStacks;
            } catch (Exception ignored) {}
        }
        return List.of();
    }
}
