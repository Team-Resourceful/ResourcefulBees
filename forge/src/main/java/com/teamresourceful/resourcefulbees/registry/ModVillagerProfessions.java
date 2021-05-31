package com.teamresourceful.resourcefulbees.registry;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModVillagerProfessions {

    private ModVillagerProfessions() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", ModPOIs.TIERED_BEEHIVE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BOTTLE_FILL));
}
