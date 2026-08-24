package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.mixin.common.StructureTemplatePoolAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;

public final class ModStructures {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.withDefaultNamespace("empty"));

    private ModStructures() {
    }

    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> pools = event.getServer()
                .registryAccess()
                .lookupOrThrow(Registries.TEMPLATE_POOL);

        Registry<StructureProcessorList> processors = event.getServer()
                .registryAccess()
                .lookupOrThrow(Registries.PROCESSOR_LIST);

        addBuildingToPool(
                pools,
                processors,
                Identifier.withDefaultNamespace("village/plains/houses"),
                ModIdentifier.of("village/beekeeper_house_1"),
                8
        );
    }

    private static void addBuildingToPool(
            Registry<StructureTemplatePool> pools,
            Registry<StructureProcessorList> processors,
            Identifier poolId,
            Identifier pieceId,
            int weight
    ) {
        Holder<StructureProcessorList> emptyProcessorList = processors.getOrThrow(EMPTY_PROCESSOR_LIST_KEY);

        StructureTemplatePool pool = pools.getValue(poolId);

        if (pool == null) {
            ModConstants.LOGGER.warn("Could not find structure template pool {}", poolId);
            return;
        }

        SinglePoolElement element = StructurePoolElement
                .legacy(pieceId.toString(), emptyProcessorList)
                .apply(StructureTemplatePool.Projection.RIGID);

        StructureTemplatePoolAccessor accessor = (StructureTemplatePoolAccessor) pool;
        List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(accessor.resourcefulbees$getRawTemplates());
        rawTemplates.add(Pair.of(element, weight));
        accessor.resourcefulbees$setRawTemplates(rawTemplates);
        ObjectArrayList<StructurePoolElement> templates = accessor.resourcefulbees$getTemplates();

        for (int i = 0; i < weight; i++) {
            templates.add(element);

            ModConstants.LOGGER.info(
                    "Pool {} after injection: raw entries={}, weighted entries={}",
                    poolId,
                    pool.getTemplates().size(),
                    pool.size()
            );

            long rawMatches = pool.getTemplates()
                    .stream()
                    .filter(pair -> pair.getFirst() == element)
                    .count();

            long weightedMatches = templates
                    .stream()
                    .filter(entry -> entry == element)
                    .count();

            ModConstants.LOGGER.info(
                    "Injected {} into {}: raw matches={}, weighted matches={}",
                    pieceId,
                    poolId,
                    rawMatches,
                    weightedMatches
            );
        }

        ModConstants.LOGGER.info("Added structure {} to pool {} with weight {}", pieceId, poolId, weight);
    }
}