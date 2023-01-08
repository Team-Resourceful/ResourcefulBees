package com.teamresourceful.resourcefulbees.centrifuge.common.entities;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeGearboxEntity extends AbstractCentrifugeEntity {
    public CentrifugeGearboxEntity(BlockPos pos, BlockState state) {
        super(CentrifugeBlockEntities.CENTRIFUGE_GEARBOX_ENTITY.get(), pos, state);
    }
}
