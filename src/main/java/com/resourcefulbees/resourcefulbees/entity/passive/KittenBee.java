package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.BaseModelTypes;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.LightLevels;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModItems;

public class KittenBee {

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.KITTEN_BEE, getKittenBeeData());
    }

    private static CustomBeeData getKittenBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.KITTEN_BEE, "all", true,
                MutationData.createDefault(),
                new ColorData.Builder(false)
                        .setPrimaryColor("#EAA939")
                        .setSecondaryColor("#4C483B")
                        .createColorData(),
                new CombatData.Builder(false)
                        .setAttackDamage(0f)
                        .setRemoveStingerOnAttack(false)
                        .create(),
                new CentrifugeData.Builder(true, "minecraft:cat_spawn_egg")
                        .setBottleOutput("resourcefulbees:catnip_honey_bottle")
                        .setBottleOutputWeight(0.1f)
                        .setMainOutputWeight(0.005f)
                        .createCentrifugeData(),
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
                .setBaseLayerTexture("/kitten/bee")
                .setMaxTimeInHive(500)
                .setBaseModelType(BaseModelTypes.KITTEN)
                .setSizeModifier(0.75f)
                .setTraits(new String[]{BeeConstants.KITTEN_BEE})
                .createCustomBee();

        data.shouldResourcefulBeesDoForgeRegistration = true;
        data.setCombRegistryObject(ModItems.CATNIP_HONEYCOMB);
        data.setCombBlockItemRegistryObject(ModItems.CATNIP_HONEYCOMB_BLOCK_ITEM);
        data.setCombBlockRegistryObject(ModBlocks.CATNIP_HONEY_BLOCK);

        return data;
    }
}
