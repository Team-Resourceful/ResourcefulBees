package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeGearboxEntity extends AbstractCentrifugeEntity {
    public CentrifugeGearboxEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE_GEARBOX_ENTITY.get(), pos, state);
    }
}
