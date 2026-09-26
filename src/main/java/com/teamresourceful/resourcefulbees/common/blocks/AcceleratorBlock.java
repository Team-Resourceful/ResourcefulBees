package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.AcceleratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;

public class AcceleratorBlock extends TickingBlock<AcceleratorBlockEntity> {

    public AcceleratorBlock(Properties properties) {
        super(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY, properties);
    }
}
