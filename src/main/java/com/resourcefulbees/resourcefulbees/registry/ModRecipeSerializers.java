package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.item.crafting.HoneyTankRecipe;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.recipe.FertiliserRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeSerializers {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge",
            () -> new CentrifugeRecipe.Serializer<>((id, ingredient, itemOutputs, fluidOutput, time, multiblockTime, multiblock, hasFluidOutput, outputBottleAndFluid) -> new CentrifugeRecipe(id, ingredient, itemOutputs, fluidOutput, time, multiblockTime, multiblock, hasFluidOutput, outputBottleAndFluid)));

    public static final RegistryObject<IRecipeSerializer<?>> FERTILIZER_RECIPE = RECIPE_SERIALIZERS.register("fertilizer_crafting",
            FertiliserRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> HONEY_TANK_RECIPE = RECIPE_SERIALIZERS.register("honey_tank_recipe", HoneyTankRecipe.Serializer::new);

//    public static final SpecialRecipeSerializer<HoneyTankRecipe> NETHER_HONEY_TANK_RECIPE = RECIPE_SERIALIZERS.register("nether_honey_tank_recipe",
//            () -> new HoneyTankRecipe(ModItems.NETHER_HONEY_TANK_ITEM.getId(), new String[]{
//                    "minecraft:nether_bricks", "minecraft:nether_bricks", "minecraft:nether_bricks",
//                    "tag:forge:glass", "wooden_honey_tank", "tag:forge:glass",
//                    "minecraft:nether_bricks", "minecraft:nether_bricks", "minecraft:nether_bricks"
//            }, ModItems.NETHER_HONEY_TANK_ITEM));
}
