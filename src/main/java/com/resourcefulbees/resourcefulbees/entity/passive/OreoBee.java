package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.LightLevels;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;

public class OreoBee {

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.OREO_BEE, getOreoBeeData());
    }

    private static CustomBeeData getOreoBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.OREO_BEE, "all", true,
                MutationData.createDefault(),
                new ColorData.Builder(false)
                        .setPrimaryColor("#442920")
                        .setSecondaryColor("#e1d9b8")
                        .createColorData(),
                new CombatData.Builder(false)
                        .setAttackDamage(0f)
                        .setRemoveStingerOnAttack(false)
                        .setBaseHealth(20f)
                        .create(),
                CentrifugeData.createDefault(),
                BreedData.createDefault(),
                new SpawnData.Builder(true)
                        .setSpawnWeight(3)
                        .setBiomeWhitelist("tag:rare")
                        .setLightLevel(LightLevels.DAY)
                        .setMinGroupSize(1)
                        .setMaxGroupSize(1)
                        .createSpawnData(),
                new TraitData(true))
                .setEasterEggBee(true)
                .setBaseLayerTexture("/oreo/oreo_bee")
                .setMaxTimeInHive(6000)
                .setSizeModifier(1.25f)
                .setTraits(new String[]{BeeConstants.OREO_BEE})
                .setApiaryOutputAmounts(new int[]{1, 2, 3, 4})
                .createCustomBee();

        data.shouldResourcefulBeesDoForgeRegistration = true;
        data.setCombRegistryObject(ModItems.OREO_COOKIE);
        data.setCombBlockItemRegistryObject(ModItems.OREO_COOKIE);

        return data;
    }
}
