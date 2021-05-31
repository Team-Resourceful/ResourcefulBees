package com.teamresourceful.resourcefulbees.registry;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ModPOIs {

    private ModPOIs() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);


    public static final RegistryObject<PoiType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PoiType("tiered_beehive_poi", ImmutableSet.copyOf(getPOIBlockStates()), 1, 1));

    private static Set<BlockState> getPOIBlockStates() {
        Set<BlockState> states = new HashSet<>();

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(ModPOIs::isTieredHiveOrApiary)
                .forEach(addAllBlockStates(states));
        return states;
    }

    @NotNull
    private static Consumer<Block> addAllBlockStates(Set<BlockState> states) {
        return block -> states.addAll(block.getStateDefinition().getPossibleStates());
    }

    private static boolean isTieredHiveOrApiary(Block block) {
        return block instanceof TieredBeehiveBlock || block instanceof ApiaryBlock;
    }
}
