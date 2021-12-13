package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractTieredCentrifugeEntity extends AbstractCentrifugeEntity {

    protected final CentrifugeTier tier;

    protected AbstractTieredCentrifugeEntity(BlockEntityType<?> tileEntityTypeIn, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.tier = tier;
    }

    public CentrifugeTier getTier() {
        return tier;
    }
}
