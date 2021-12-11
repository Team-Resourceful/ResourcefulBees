package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyEffect;
import com.resourcefulbees.resourcefulbees.lib.*;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.potion.Effects;

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
                        .setPrimaryColor("#EAA939")
                        .setSecondaryColor("#4C483B")
                        .setModelType(ModelTypes.BUNNY)
                        .createColorData(),
                new CombatData.Builder(true)
                        .setAttackDamage(0f)
                        .setRemoveStingerOnAttack(false)
                        .setBaseHealth(20f)
                        .create(),
                new CentrifugeData.Builder(false, null)
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
                .setBaseLayerTexture("/abbee/bee")
                .setMaxTimeInHive(500)
                .setBaseModelType(BaseModelTypes.THICK_LEGS)
                .setSizeModifier(0.75f)
                .setTraits(new String[]{TraitConstants.CLINGY})
                .setCreator("Dawn Felstar")
                .setLore("Cute little baby kitty bee.")
                .setLoreColor(BeeConstants.RAINBOW_COLOR)
                .createCustomBee();

        data.setShouldResourcefulBeesDoForgeRegistration(true);
        data.setCombRegistryObject(ModItems.CATNIP_HONEYCOMB);
        data.setCombBlockItemRegistryObject(ModItems.CATNIP_HONEYCOMB_BLOCK_ITEM);
        data.setCombBlockRegistryObject(ModBlocks.CATNIP_HONEYCOMB_BLOCK);

        return data;
    }
}
