package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.components.Bees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponents {

    public static final ResourcefulRegistry<DataComponentType<?>> COMPONENTS = RegistryHelper.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<DataComponentType<Bees>> BEES = COMPONENTS.register("bees", () -> DataComponentType.<Bees>builder()
            .persistent(Bees.CODEC)
            .networkSynchronized(Bees.STREAM_CODEC)
            .cacheEncoding()
            .build()
    );
}
