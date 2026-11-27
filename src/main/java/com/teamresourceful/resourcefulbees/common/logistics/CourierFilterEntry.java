package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public record CourierFilterEntry(Identifier identifier) {

    public static final Codec<CourierFilterEntry> CODEC = Identifier.CODEC.xmap(CourierFilterEntry::new, CourierFilterEntry::identifier);

    public boolean matches(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).equals(identifier);
    }

    public boolean matches(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid).equals(identifier);
    }
}