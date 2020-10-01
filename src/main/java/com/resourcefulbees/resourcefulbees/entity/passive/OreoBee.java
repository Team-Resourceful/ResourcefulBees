package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.LightLevels;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;

public class OreoBee {

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.OREO_BEE, getOreoBeeData());
    }

    private static CustomBeeData getOreoBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.OREO_BEE, "all", true,
                new MutationData.Builder(false, MutationTypes.NONE).createMutationData(),
                new ColorData.Builder(false)
                        .setPrimaryColor("#442920")
                        .setSecondaryColor("#e1d9b8")
                        .createColorData(),
                new CentrifugeData.Builder(false, null).createCentrifugeData(),
                new BreedData.Builder(false).createBreedData(),
                new SpawnData.Builder(true)
                        .setSpawnWeight(6)
                        .setBiomeWhitelist("tag:rare")
                        .setLightLevel(LightLevels.ANY)
                        .setMinGroupSize(1)
                        .setMaxGroupSize(1)
                        .createSpawnData(),
                new TraitData(true))
                .setBaseLayerTexture("/oreo/oreo_bee")
                .setMaxTimeInHive(6000)
                .setSizeModifier(1.25f)
                .setAttackDamage(0)
                .setTraits(new String[]{BeeConstants.OREO_BEE})
                .createCustomBee();

        data.shouldResourcefulBeesDoForgeRegistration = true;
        data.setCombRegistryObject(RegistryHandler.OREO_COOKIE);
        data.setCombBlockItemRegistryObject(RegistryHandler.OREO_COOKIE);

        return data;
    }
}
