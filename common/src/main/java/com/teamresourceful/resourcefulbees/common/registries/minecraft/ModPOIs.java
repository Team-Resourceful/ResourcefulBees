package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.stream.Collectors;

public final class ModPOIs {

    private ModPOIs() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<PoiType> POIS = ResourcefulRegistries.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<PoiType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PoiType(getPOIBlockStates(), 1, 1));

    private static Set<BlockState> getPOIBlockStates() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(RegistryEntry::get)
                .filter(block -> block instanceof BeeHolderBlock)
                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                .collect(Collectors.toUnmodifiableSet());
    }
}