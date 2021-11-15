package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;

public class CentrifugeProcessorEntity extends AbstractCentrifugeEntity {
    public CentrifugeProcessorEntity() {
        super(ModBlockEntityTypes.CENTRIFUGE_PROCESSOR_ENTITY.get());
    }
}
