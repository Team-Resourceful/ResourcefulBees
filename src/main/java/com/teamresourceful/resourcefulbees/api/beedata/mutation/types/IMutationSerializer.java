package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.mojang.serialization.Codec;

public interface IMutationSerializer {

    Codec<? extends IMutation> codec();

    String id();
}
