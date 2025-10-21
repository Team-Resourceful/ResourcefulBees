package com.teamresourceful.resourcefulbees.mixin.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SpawnEggItem.class)
public interface SpawnEggItemAccessor {

    @Accessor("BY_ID")
    static Map<EntityType<? extends Mob>, SpawnEggItem> getById() {
        throw new AssertionError();
    }
}
