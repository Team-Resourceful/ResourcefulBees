package com.teamresourceful.resourcefulbees.client.fluids;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid.CustomHoneyFluidData;
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties;
import com.teamresourceful.resourcefullib.client.registry.ResourcefulClientRegistries;
import com.teamresourceful.resourcefullib.client.registry.ResourcefulClientRegistryType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;

public class ModClientFluidProperties {
    public static final ResourcefulRegistry<ClientFluidProperties> CLIENT_FLUID_PROPERTIES = ResourcefulClientRegistries.create(ResourcefulClientRegistryType.FLUID, ModConstants.MOD_ID);
    public static final RegistryEntry<ClientFluidProperties> HONEY_CLIENT_PROPERTIES = CLIENT_FLUID_PROPERTIES.register("honey", HoneyClientFluidProperties::create);

    public static void registerHoneyFluids() {
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((id, honeyData) ->
                honeyData
                        .getOptionalData(CustomHoneyFluidData.SERIALIZER)
                        .ifPresent(fluidData ->
                                CLIENT_FLUID_PROPERTIES.register(
                                        id + "_honey",
                                        () -> CustomHoneyClientFluidProperties.create(
                                                fluidData.renderData()
                                        )
                                )
                        )
        );
    }
}
