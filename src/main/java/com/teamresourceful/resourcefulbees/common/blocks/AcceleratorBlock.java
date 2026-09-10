package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.AcceleratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

public class AcceleratorBlock extends TickingBlock<AcceleratorBlockEntity> {

    private static final MapCodec<AcceleratorBlock> CODEC = BlockBehaviour.simpleCodec(AcceleratorBlock::new);

    public AcceleratorBlock(Properties properties) {
        super(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY, properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
