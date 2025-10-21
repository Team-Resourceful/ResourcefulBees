package com.teamresourceful.resourcefulbees.api.data.honey.fluid;

import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.resources.ResourceLocation;

public interface HoneyRenderData {

    Color color();

    ResourceLocation still();

    ResourceLocation flowing();

    ResourceLocation face();

    ResourceLocation overlay();

}
