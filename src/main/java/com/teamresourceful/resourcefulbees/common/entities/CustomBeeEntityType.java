package com.teamresourceful.resourcefulbees.common.entities;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.flag.FeatureFlags;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class CustomBeeEntityType<T extends Bee> extends EntityType<T> {

    private final String beeType;

    public CustomBeeEntityType(String beeType, EntityFactory<T> factory, EntityDimensions dimensions) {
        super(factory,
                MobCategory.valueOf("RESOURCEFULBEES_BEE"),
                true,
                true,
                false,
                false,
                ImmutableSet.of(),
                dimensions,
                .5f,
                5,
                3,
                String.format("Resourceful Bees: %s Bee", WordUtils.capitalize(beeType)),
                Optional.empty(),
                FeatureFlags.DEFAULT_FLAGS,
                true);
        this.beeType = beeType;
    }

    public static <T extends Bee> CustomBeeEntityType<T> of(String beeType, EntityFactory<T> factory, float width, float height) {
        return new CustomBeeEntityType<>(beeType, factory, EntityDimensions.scalable(width, height));
    }

    public String getBeeType() {
        return beeType;
    }

    public CustomBeeData getData() {
        return BeeRegistry.get().getBeeData(beeType);
    }
}
