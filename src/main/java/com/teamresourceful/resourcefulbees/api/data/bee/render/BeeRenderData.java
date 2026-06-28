package com.teamresourceful.resourcefulbees.api.data.bee.render;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.resources.Identifier;

import java.util.Set;

public interface BeeRenderData extends BeeData<BeeRenderData> {

    Set<BeeLayerData> layers();

    BeeColorData colorData();

    Identifier model();

    BeeLayerTexture texture();

    Identifier animation();

    float sizeModifier();

    float pulseFrequency();
}
