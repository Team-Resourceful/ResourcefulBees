package com.teamresourceful.resourcefulbees.api.data.bee.render;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface BeeRenderData extends BeeData<BeeRenderData> {

    Set<BeeLayerData> layers();

    BeeColorData colorData();

    ResourceLocation model();

    BeeLayerTexture texture();

    ResourceLocation animations();

    float sizeModifier();
}
