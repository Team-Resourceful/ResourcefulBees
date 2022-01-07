package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.Mutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.MutationOutput;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyEffect;
import com.resourcefulbees.resourcefulbees.lib.*;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effects;

public class StarryBee {

    private StarryBee() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void register() {
        BeeRegistry.getRegistry().registerBee(BeeConstants.STARRY_BEE, getOreoBeeData());
        BeeRegistry.getRegistry().registerHoney(BeeConstants.STARRY_BEE, getHoneyBottleData());
    }

    private static final String COLOR = "#FFA500";

    private static CustomBeeData getOreoBeeData() {
        CustomBeeData data = new CustomBeeData.Builder(BeeConstants.STARRY_BEE, "minecraft:beacon", true,
                new MutationData.Builder(true, MutationTypes.ITEM)
                        .addMutation(new Mutation(MutationTypes.ITEM,
                                "resourcefulbees:starry_honey_block",
                                0.1f,
                                new MutationOutput("minecraft:nether_star", 1, null)))
                        .createMutationData(),
                new ColorData.Builder(true)
                        .setPrimaryColor("#FFFFFF")
                        .setSecondaryColor(COLOR)
                        .setHoneycombColor(COLOR)
                        .setIsGlowing(true)
                        .setGlowingPulse(1)
                        .setGlowColor(COLOR)
                        .setPrimaryLayerTexture("starry/starry_bee_primary_layer")
                        .setSecondaryLayerTexture("empty_layer")
                        .setEmissiveLayerTexture("starry/starry_bee_emissive")
                        .setModelType(ModelTypes.QUEEN)
                        .createColorData(),
                new CombatData.Builder(false)
                        .setIsInvulnerable(true)
                        .setAttackDamage(0f)
                        .setRemoveStingerOnAttack(false)
                        .setInflictsPoison(false)
                        .setBaseHealth(20f)
                        .create(),
                new CentrifugeData.Builder(true, "resourcefulbees:starry_honey")
                        .setMainOutputWeight(0.1f)
                        .setMainOutputCount(5)
                        .setBottleOutputWeight(0.1f)
                        .setRecipeTime(200)
                        .setHasFluidOutput(true)
                        .createCentrifugeData(),
                BreedData.createDefault(),
                new SpawnData.Builder(true)
                        .setSpawnWeight(2)
                        .setBiomeWhitelist("minecraft:small_end_islands")
                        .setLightLevel(LightLevels.ANY)
                        .setMinGroupSize(1)
                        .setMaxGroupSize(1)
                        .setMinYLevel(50)
                        .setMaxYLevel(80)
                        .createSpawnData(),
                new TraitData(true))
                .setEasterEggBee(true)
                .setBaseLayerTexture("empty_layer")
                .setMaxTimeInHive(1200)
                .setSizeModifier(1.25f)
                .setBaseModelType(BaseModelTypes.THICK_LEGS)
                .setCreator("Lady Lexxie")
                .setLore("Reach for the stars!")
                .setLoreColor("rainbow")
                .setApiaryOutputTypes(new ApiaryOutput[]{ApiaryOutput.COMB, ApiaryOutput.COMB, ApiaryOutput.COMB, ApiaryOutput.COMB, ApiaryOutput.COMB})
                .setApiaryOutputAmounts(new int[]{1, 1, 1, 1, 1})
                .setTraits(new String[]{BeeConstants.STARRY_BEE})
                .setHoneycombRarity(Rarity.EPIC.toString())
                .createCustomBee();

        data.setShouldResourcefulBeesDoForgeRegistration(true);
        //data.setCombRegistryObject(ModItems.STARRY_HONEYCOMB);
        //data.setCombBlockItemRegistryObject(ModItems.STARRY_HONEYCOMB_BLOCK_ITEM);
        //data.setCombBlockRegistryObject(ModBlocks.STARRY_HONEYCOMB_BLOCK);

        return data;
    }

    private static HoneyBottleData honeyBottleData = null;

    public static HoneyBottleData getHoneyBottleData() {
        if (honeyBottleData == null) {
            HoneyBottleData.Builder builder = new HoneyBottleData.Builder("starry", 20, 1f, "#E36E1B");
            HoneyEffect glowing = new HoneyEffect(Effects.GLOWING.getRegistryName().toString(), 2400, 0, 1);
            builder.addEffect(glowing);
            honeyBottleData = builder.build();
            honeyBottleData.setShouldResourcefulBeesDoForgeRegistration(true);
        }
        return honeyBottleData;
    }
}