package com.teamresourceful.resourcefulbees.centrifuge.common.entities;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeProcessorEntity extends AbstractCentrifugeEntity {
    public CentrifugeProcessorEntity(BlockPos pos, BlockState state) {
        super(CentrifugeBlockEntities.CENTRIFUGE_PROCESSOR_ENTITY.get(), pos, state);
    }
}
