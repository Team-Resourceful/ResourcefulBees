package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.recipe.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeSerializers {

    private ModRecipeSerializers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge",
            () -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));

    public static final RegistryObject<IRecipeSerializer<?>> FERTILIZER_RECIPE = RECIPE_SERIALIZERS.register("fertilizer_crafting",
            FertiliserRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> HONEY_TANK_RECIPE = RECIPE_SERIALIZERS.register("honey_tank_recipe", HoneyTankRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> APIARY_UPGRADE_RECIPE = RECIPE_SERIALIZERS.register("hive_upgrade_recipe", HiveUpgradeRecipe.Serializer::new);
}
