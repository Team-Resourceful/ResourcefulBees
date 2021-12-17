package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.*;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;

public class YetiBee {

    private YetiBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.YETI_BEE, getOreoBeeData());
    }

    private static CustomBeeData getOreoBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.YETI_BEE, "minecraft:snow_block", false,
                new MutationData.Builder(true, MutationTypes.BLOCK)
                        .setMutationInput("minecraft:water")
                        .setMutationOutput("minecraft:ice")
                        .setMutationCount(30)
                        .createMutationData(),
                new ColorData.Builder(false)
                        .setPrimaryColor("#E9F4F6")
                        .setSecondaryColor("#777E86")
                        .setModelType(ModelTypes.YETI)
                        .createColorData(),
                new CombatData.Builder(false)
                        .setAttackDamage(0f)
                        .setRemoveStingerOnAttack(false)
                        .setBaseHealth(20f)
                        .create(),
                CentrifugeData.createDefault(),
                BreedData.createDefault(),
                new SpawnData.Builder(true)
                        .setSpawnWeight(2)
                        .setBiomeWhitelist("tag:snowy")
                        .setLightLevel(LightLevels.DAY)
                        .setMinGroupSize(1)
                        .setMaxGroupSize(1)
                        .createSpawnData(),
                new TraitData(true))
                .setEasterEggBee(true)
                .setBaseLayerTexture("/yeti/yeti_bee")
                .setMaxTimeInHive(6000)
                .setSizeModifier(1.25f)
                .setCreator("Joosh")
                .setLore("A pretty \u00A7ocool\u00A7r bee.\nHe's here to bee your best friend :)")
                .setLoreColor("#ADD8E6")
                .setTraits(new String[]{BeeConstants.OREO_BEE})
                .createCustomBee();

        data.setShouldResourcefulBeesDoForgeRegistration(true);

        return data;
    }
}
