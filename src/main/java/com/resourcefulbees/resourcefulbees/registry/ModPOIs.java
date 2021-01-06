package com.resourcefulbees.resourcefulbees.registry;

import com.google.common.collect.ImmutableSet;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import net.minecraft.block.BlockState;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class ModPOIs {

    public static final DeferredRegister<PointOfInterestType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<PointOfInterestType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PointOfInterestType("tiered_beehive_poi", ImmutableSet.copyOf(getPOIBlockStates()), 1, 1));

    private static Set<BlockState> getPOIBlockStates() {
        Set<BlockState> states = new HashSet<>();

        ModBlocks.BLOCKS.getEntries().stream()
                .filter(blockRegistryObject -> blockRegistryObject.get() instanceof TieredBeehiveBlock || blockRegistryObject.get() instanceof ApiaryBlock)
                .forEach((blockRegistryObject -> states.addAll(blockRegistryObject.get()
                        .getStateContainer()
                        .getValidStates())));
        return states;
    }
}
