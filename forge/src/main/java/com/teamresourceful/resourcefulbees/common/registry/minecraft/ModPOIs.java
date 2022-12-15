package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.block.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.block.FlowHiveBlock;
import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.stream.Collectors;

public final class ModPOIs {

    private ModPOIs() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<PoiType> POIS = ResourcefulRegistries.create(Registry.POINT_OF_INTEREST_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<PoiType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PoiType(getPOIBlockStates(), 1, 1));

    private static Set<BlockState> getPOIBlockStates() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(RegistryEntry::get)
                .filter(ModPOIs::isTieredHiveOrApiary)
                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean isTieredHiveOrApiary(Block block) {
        return block instanceof TieredBeehiveBlock || block instanceof ApiaryBlock || block instanceof FlowHiveBlock;
    }
}
