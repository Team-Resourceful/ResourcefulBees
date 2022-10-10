package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;

public class CustomBeeEntityType<T extends ResourcefulBee> extends EntityType<T> {

    private final String beeType;

    public CustomBeeEntityType(String beeType, EntityFactory<T> factory, EntityDimensions dimensions) {
        super(factory, ModConstants.BEE_MOB_CATEGORY, true, true, false, false, ImmutableSet.of(), dimensions, 5, 3);
        this.beeType = beeType;
    }

    public static <T extends ResourcefulBee> CustomBeeEntityType<T> of(String beeType, EntityFactory<T> factory, float width, float height) {
        return new CustomBeeEntityType<>(beeType, factory, EntityDimensions.scalable(width, height));
    }

    public String getBeeType() {
        return beeType;
    }

    public CustomBeeData getData() {
        return BeeRegistry.getRegistry().getBeeData(beeType);
    }
}
