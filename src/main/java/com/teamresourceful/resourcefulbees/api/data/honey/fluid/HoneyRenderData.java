package com.teamresourceful.resourcefulbees.api.data.honey.fluid;

import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.resources.Identifier;

public interface HoneyRenderData {

    Color color();

    Identifier still();

    Identifier flowing();

    Identifier face();

    Identifier overlay();

}
