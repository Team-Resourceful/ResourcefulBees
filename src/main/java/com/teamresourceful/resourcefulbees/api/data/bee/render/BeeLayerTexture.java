package com.teamresourceful.resourcefulbees.api.data.bee.render;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.NeutralMob;

public interface BeeLayerTexture {

    Identifier texture();

    Identifier angryTexture();

    Identifier getTexture(NeutralMob neutralMob);

    default String id() {
        return texture().getPath().substring("textures/entity/".length(), texture().getPath().length() - 4);
    }
}
