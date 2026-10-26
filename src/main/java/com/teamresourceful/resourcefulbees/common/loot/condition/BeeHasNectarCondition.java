package com.teamresourceful.resourcefulbees.common.loot.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

public record BeeHasNectarCondition() implements LootItemCondition {

    public static final BeeHasNectarCondition INSTANCE = new BeeHasNectarCondition();

    public static final MapCodec<BeeHasNectarCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getOptional(LootContextParams.THIS_ENTITY);

        return entity instanceof Bee bee && bee.hasNectar();
    }

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }
}