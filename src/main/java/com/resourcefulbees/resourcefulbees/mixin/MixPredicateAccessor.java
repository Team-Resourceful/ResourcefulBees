package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionBrewing.MixPredicate.class)
public interface MixPredicateAccessor {

    @Accessor("ingredient")
    Ingredient getIngredient();
}
