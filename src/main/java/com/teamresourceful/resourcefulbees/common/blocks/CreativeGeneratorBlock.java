package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.CreativeGenBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;

public class CreativeGeneratorBlock extends TickingBlock<CreativeGenBlockEntity> {

    public CreativeGeneratorBlock(Properties properties) {
        super(ModBlockEntityTypes.CREATIVE_GEN_ENTITY, properties);
    }

}
