package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.HolderRegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.stream.Collectors;

public final class ModPOIs {

    private ModPOIs() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<PoiType> POIS = RegistryHelper.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, ModConstants.MOD_ID);

    public static final ResourceKey<PoiType> TIERED_BEEHIVE_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ModIdentifier.of("tiered_beehive_poi"));
    public static final HolderRegistryEntry<PoiType> TIERED_BEEHIVE_POI = POIS.registerHolder("tiered_beehive_poi", () -> new PoiType(getPOIBlockStates(), 1, 1));

    private static Set<BlockState> getPOIBlockStates() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(RegistryEntry::get)
                .filter(BeeHolderBlock.class::isInstance)
                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                .collect(Collectors.toUnmodifiableSet());
    }
}