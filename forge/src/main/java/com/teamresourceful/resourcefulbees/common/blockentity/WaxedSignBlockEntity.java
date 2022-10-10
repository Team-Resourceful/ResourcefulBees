package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WaxedSignBlockEntity extends SignBlockEntity {
    public WaxedSignBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(pWorldPosition, pBlockState);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ModBlockEntityTypes.WAXED_SIGN_ENTITY.get();
    }
}
