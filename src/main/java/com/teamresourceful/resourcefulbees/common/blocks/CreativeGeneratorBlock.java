package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.CreativeGenBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

public class CreativeGeneratorBlock extends TickingBlock<CreativeGenBlockEntity> {

    private static final MapCodec<CreativeGeneratorBlock> CODEC = BlockBehaviour.simpleCodec(CreativeGeneratorBlock::new);

    public CreativeGeneratorBlock(Properties properties) {
        super(ModBlockEntityTypes.CREATIVE_GEN_ENTITY, properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
