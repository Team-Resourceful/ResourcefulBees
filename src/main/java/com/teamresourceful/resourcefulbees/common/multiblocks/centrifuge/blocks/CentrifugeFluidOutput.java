package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeFluidOutputEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeFluidOutput extends AbstractCentrifuge {

    private final RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> tileType;

    public CentrifugeFluidOutput(@NotNull Properties properties, RegistryObject<TileEntityType<CentrifugeFluidOutputEntity>> tileType) {
        super(properties);
        this.tileType = tileType;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileType.get().create();
    }

    @Override
    public boolean usesFaceDirection() {
        return true;
    }
}
