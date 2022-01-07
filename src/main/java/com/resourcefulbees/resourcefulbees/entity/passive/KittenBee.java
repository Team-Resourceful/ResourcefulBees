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

public class KittenBee {

    private KittenBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.KITTEN_BEE, getKittenBeeData());
    }

    private static CustomBeeData getKittenBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.KITTEN_BEE, "tag:minecraft:beds", true,
                MutationData.createDefault(),
                new ColorData.Builder(false)
                        .setPrimaryColor("#EAA939")
                        .setSecondaryColor("#4C483B")
                        .setModelType(ModelTypes.KITTEN)
                        .createColorData(),
                new CombatData.Builder(false)
                        .setAttackDamage(0f)
                        .setRemoveStingerOnAttack(false)
                        .setBaseHealth(20f)
                        .create(),
                new CentrifugeData.Builder(true, "minecraft:cat_spawn_egg")
                        .setBottleOutput("resourcefulbees:catnip_honey_bottle")
                        .setBottleOutputWeight(0.1f)
                        .setMainOutputWeight(0.01f)
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
                .setBaseModelType(BaseModelTypes.THICK_LEGS)
                .setSizeModifier(0.75f)
                .setTraits(new String[]{BeeConstants.KITTEN_BEE})
                .setCreator("Dawn Felstar")
                .setLore("Cute little baby kitty bee.")
                .setLoreColor(BeeConstants.RAINBOW_COLOR)
                .hasCustomDrop()
                .createCustomBee();

        data.setShouldResourcefulBeesDoForgeRegistration(true);
        data.setCombRegistryObject(ModItems.CATNIP_HONEYCOMB);
        data.setCombBlockItemRegistryObject(ModItems.CATNIP_HONEYCOMB_BLOCK_ITEM);
        data.setCombBlockRegistryObject(ModBlocks.CATNIP_HONEYCOMB_BLOCK);

        return data;
    }

    private static HoneyBottleData honeyBottleData = null;

    public static HoneyBottleData getHoneyBottleData() {
        if (honeyBottleData == null) {
            HoneyBottleData.Builder builder = new HoneyBottleData.Builder("catnip", 8, 0.9f, "#BD5331");
            HoneyEffect speed = new HoneyEffect(Effects.MOVEMENT_SPEED.getRegistryName().toString(), 2400, 2, 1);
            HoneyEffect nightVision = new HoneyEffect(Effects.NIGHT_VISION.getRegistryName().toString(), 2400, 0, 1);
            HoneyEffect jump = new HoneyEffect(Effects.JUMP.getRegistryName().toString(), 2400, 1, 1);
            builder.addEffect(speed).addEffect(nightVision).addEffect(jump);
            honeyBottleData = builder.build();
            honeyBottleData.setHoneyBlockRegistryObject(ModBlocks.CATNIP_HONEY_BLOCK);
            honeyBottleData.setHoneyStillFluidRegistryObject(ModFluids.CATNIP_HONEY_STILL);
            honeyBottleData.setHoneyFlowingFluidRegistryObject(ModFluids.CATNIP_HONEY_FLOWING);
            honeyBottleData.setHoneyBlockItemRegistryObject(ModItems.CATNIP_HONEY_BLOCK_ITEM);
            honeyBottleData.setHoneyBucketItemRegistryObject(ModItems.CATNIP_HONEY_FLUID_BUCKET);
            honeyBottleData.setHoneyBottleRegistryObject(ModItems.CATNIP_HONEY_BOTTLE);
        }
        return honeyBottleData;
    }
}
