package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.npc.VillagerProfession;

public final class ModVillagerProfessions {

    private ModVillagerProfessions() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<VillagerProfession> PROFESSIONS = RegistryHelper.create(BuiltInRegistries.VILLAGER_PROFESSION, ModConstants.MOD_ID);

    public static final RegistryEntry<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", poi -> poi.is(PoiTypeTags.BEE_HOME), poi -> poi.is(PoiTypeTags.BEE_HOME), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.BOTTLE_FILL));
}
