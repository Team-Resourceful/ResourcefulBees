package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import net.minecraft.resources.Identifier;

public interface Parents {

    Identifier getParent1();

    Identifier getParent2();

    default ParentPair parentPair() {
        return ParentPair.of(getParent1(), getParent2());
    }

    CustomBeeData getParent1Data();

    CustomBeeData getParent2Data();
}
