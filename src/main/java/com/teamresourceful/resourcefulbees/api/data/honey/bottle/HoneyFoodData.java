package com.teamresourceful.resourcefulbees.api.data.honey.bottle;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

import static net.minecraft.world.item.component.Consumables.defaultDrink;

public interface HoneyFoodData {

    int nutrition();

    float saturation();

    boolean canAlwaysEat();

    float consumeSeconds();

    //todo effects are now done as food consumables see Consumable.java
    List<HoneyBottleEffectData> effects();

    default FoodProperties getFood() {
        var builder = new FoodProperties.Builder();
        builder.nutrition(nutrition());
        builder.saturationModifier(saturation());
        if (canAlwaysEat()) builder.alwaysEdible();
        //todo if (fastFood()) builder.fast();
        //todo effects().forEach(effect -> builder.effect(effect.getInstance(), effect.chance()));
        return builder.build();
    }

    default Consumable getConsumable() {
        var builder = defaultDrink().consumeSeconds(consumeSeconds()).sound(SoundEvents.HONEY_DRINK);
        effects().forEach(effect -> builder.onConsume(new ApplyStatusEffectsConsumeEffect(effect.getInstance(), effect.chance())));
        return builder.build();
    }
}
