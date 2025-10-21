package com.teamresourceful.resourcefulbees.api.data.bee.render;

import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
import com.teamresourceful.resourcefullib.common.color.Color;

public interface BeeLayerData {

    Color color();

    BeeLayerTexture texture();

    LayerEffect effect();

    boolean pollenLayer();

    float pulseFrequency();
}
