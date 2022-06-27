package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModVillagerProfessions {

    private ModVillagerProfessions() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ResourcefulBees.MOD_ID);


    public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", poi -> poi.is(PoiTypeTags.BEE_HOME), poi -> poi.is(PoiTypeTags.BEE_HOME), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BOTTLE_FILL));
}
