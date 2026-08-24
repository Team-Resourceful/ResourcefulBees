package com.teamresourceful.resourcefulbees.common.entities;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.flag.FeatureFlags;

import java.util.Optional;

public class CustomBeeEntityType<T extends Bee> extends EntityType<T> {

    private final Identifier beeType;

    public CustomBeeEntityType(Identifier beeType, EntityFactory<T> factory, EntityDimensions dimensions) {
        super(factory,
                ModConstants.RESOURCEFUL_BEE_CATEGORY,
                true,
                true,
                false,
                false,
                TagKey.create(ResourceKey.createRegistryKey(ModIdentifier.of("")), ModIdentifier.of("")),
                dimensions,
                .5f,
                5,
                3,
                String.format("entity_type.resourcefulbees.%s", beeType.getPath()),
                Optional.empty(),
                FeatureFlags.DEFAULT_FLAGS,
                true);
        this.beeType = beeType;
    }

    public static <T extends Bee> CustomBeeEntityType<T> of(Identifier beeType, EntityFactory<T> factory, float width, float height) {
        return new CustomBeeEntityType<>(beeType, factory, EntityDimensions.scalable(width, height));
    }

    public Identifier getBeeType() {
        return beeType;
    }

    public CustomBeeData getData() {
        return BeeRegistry.get().getBeeData(beeType);
    }
}
