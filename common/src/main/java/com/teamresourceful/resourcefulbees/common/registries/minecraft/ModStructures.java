package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.mixin.common.StructureTemplatePoolAccessor;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.ServerGoingToStartEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Taken from: <a href="https://gist.github.com/TelepathicGrunt/4fdbc445ebcbcbeb43ac748f4b18f342">https://gist.github.com/TelepathicGrunt/4fdbc445ebcbcbeb43ac748f4b18f342</a>
 */
public final class ModStructures {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registry.PROCESSOR_LIST_REGISTRY, new ResourceLocation("empty"));

    private static void addBuildingToPool(Registry<StructureTemplatePool> pools, Registry<StructureProcessorList> processors, ResourceLocation pool, ResourceLocation piece, int weight) {
        Holder<StructureProcessorList> emptyProcessorList = processors.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        StructureTemplatePoolAccessor templatePool = (StructureTemplatePoolAccessor) pools.get(pool);

        if (templatePool == null) return;

        SinglePoolElement element = SinglePoolElement.legacy(piece.toString(), emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            templatePool.getTemplates().add(element);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(templatePool.getRawTemplates());
        listOfPieceEntries.add(new Pair<>(element, weight));
        templatePool.setRawTemplates(listOfPieceEntries);
    }

    public static void addStructures(ServerGoingToStartEvent event) {
        Registry<StructureTemplatePool> pools = event.access().registry(Registry.TEMPLATE_POOL_REGISTRY).orElseThrow();
        Registry<StructureProcessorList> processors = event.access().registry(Registry.PROCESSOR_LIST_REGISTRY).orElseThrow();

        addBuildingToPool(pools, processors,
                new ResourceLocation("village/plains/houses"),
                new ResourceLocation(ModConstants.MOD_ID, "village/beekeeper_house_1"), 8);
    }

}
