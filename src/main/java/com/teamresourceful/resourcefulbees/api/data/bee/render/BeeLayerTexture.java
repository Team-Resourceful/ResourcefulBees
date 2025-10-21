package com.teamresourceful.resourcefulbees.api.data.bee.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.NeutralMob;

public interface BeeLayerTexture {

    ResourceLocation texture();

    ResourceLocation angryTexture();

    ResourceLocation getTexture(NeutralMob neutralMob);

    default String id() {
        return texture().getPath().substring("textures/entity/".length(), texture().getPath().length() - 4);
    }
}
