package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.npc.VillagerProfession;

public final class ModVillagerProfessions {

    private ModVillagerProfessions() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final ResourcefulRegistry<VillagerProfession> PROFESSIONS = ResourcefulRegistries.create(Registry.VILLAGER_PROFESSION, ResourcefulBees.MOD_ID);

    public static final RegistryEntry<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", poi -> poi.is(PoiTypeTags.BEE_HOME), poi -> poi.is(PoiTypeTags.BEE_HOME), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BOTTLE_FILL));
}
