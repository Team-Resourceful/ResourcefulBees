package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.lib.*;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;

public class AbBee {

    private AbBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.ABBEE_BEE, getBunnyBeeData());
    }

    private static CustomBeeData getBunnyBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.ABBEE_BEE, "tag:minecraft:beds", true,
                MutationData.createDefault(),
                new ColorData.Builder(false)
                        .setPrimaryColor("#E5DED5")
                        .setSecondaryColor("#FFB1C5")
                        .setModelType(ModelTypes.BUNNY)
                        .createColorData(),
                new CombatData.Builder(true)
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
                .setBaseLayerTexture("/abbee/bee")
                .setMaxTimeInHive(500)
                .setBaseModelType(BaseModelTypes.THICK_LEGS)
                .setSizeModifier(0.75f)
                .setTraits(new String[]{TraitConstants.CLINGY})
                .setCreator("absent-")
                .setLore("Bunny Bun Bun.")
                .setLoreColor(BeeConstants.RAINBOW_COLOR)
                .setApiaryOutputAmounts(new int[]{1, 2, 3, 4})
                .createCustomBee();

        data.setShouldResourcefulBeesDoForgeRegistration(true);
        data.setCombRegistryObject(ModItems.STRAWBEERRY_MILKSHAKE);
        data.setCombBlockItemRegistryObject(ModItems.STRAWBEERRY_MILKSHAKE);

        return data;
    }
}
