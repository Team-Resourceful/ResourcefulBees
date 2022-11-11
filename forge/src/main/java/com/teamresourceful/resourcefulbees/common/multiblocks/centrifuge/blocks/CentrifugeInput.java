package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CentrifugeInput extends AbstractGUICentrifuge implements IAssemblyStateBlock {

    private final Supplier<BlockEntityType<CentrifugeInputEntity>> entityType;

    public CentrifugeInput(@NotNull Properties properties, Supplier<BlockEntityType<CentrifugeInputEntity>> entityType) {
        super(properties);
        this.entityType = entityType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return entityType.get().create(pos, state);
    }
}
