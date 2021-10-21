package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;

public class CentrifugeGearboxEntity extends AbstractCentrifugeEntity {
    public CentrifugeGearboxEntity() {
        super(ModBlockEntityTypes.CENTRIFUGE_GEARBOX_ENTITY.get());
    }
}
