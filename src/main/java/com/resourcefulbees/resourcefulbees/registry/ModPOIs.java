package com.resourcefulbees.resourcefulbees.registry;

import com.google.common.collect.ImmutableSet;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPOIs {

    public static final DeferredRegister<PointOfInterestType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<PointOfInterestType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PointOfInterestType("tiered_beehive_poi", ImmutableSet.copyOf(ModBlocks.T1_BEEHIVE.get().getStateContainer().getValidStates()), 1, 1));
}
