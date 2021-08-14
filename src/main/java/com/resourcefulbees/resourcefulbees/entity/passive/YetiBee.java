package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.LightLevels;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;

public class YetiBee {

    private YetiBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.YETI_BEE, getOreoBeeData());
    }

    private static CustomBeeData getOreoBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.YETI_BEE, "minecraft:snow_block", true,
                MutationData.createDefault(),
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
                        .setSpawnWeight(3)
                        .setBiomeWhitelist("tag:rare")
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
                .setLore("A pretty §ocool§r bee.\nHe's here to bee your best friend :)")
                .setLoreColor("#ADD8E6")
                .setTraits(new String[]{BeeConstants.OREO_BEE})
                .setApiaryOutputAmounts(new int[]{1, 2, 3, 4})
                .createCustomBee();

        data.setShouldResourcefulBeesDoForgeRegistration(true);
        data.setCombRegistryObject(ModItems.OREO_COOKIE);
        data.setCombBlockItemRegistryObject(ModItems.OREO_COOKIE);

        return data;
    }
}
