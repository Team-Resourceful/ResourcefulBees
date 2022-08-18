package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.stream.Collectors;

public final class ModPOIs {

    private ModPOIs() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<PoiType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PoiType(getPOIBlockStates(), 1, 1));

    private static Set<BlockState> getPOIBlockStates() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(ModPOIs::isTieredHiveOrApiary)
                .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean isTieredHiveOrApiary(Block block) {
        return block instanceof TieredBeehiveBlock || block instanceof ApiaryBlock;
    }
}
