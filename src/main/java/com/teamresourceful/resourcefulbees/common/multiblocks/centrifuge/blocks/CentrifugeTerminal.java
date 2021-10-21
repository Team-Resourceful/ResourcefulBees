package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeTerminalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeTerminal extends AbstractCentrifuge {

    private final RegistryObject<TileEntityType<CentrifugeTerminalEntity>> entityType;

    public CentrifugeTerminal(@NotNull Properties properties, RegistryObject<TileEntityType<CentrifugeTerminalEntity>> entityType) {
        super(properties);
        this.entityType = entityType;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return entityType.get().create();
    }

    @Override
    public boolean usesCentrifugeState() {
        return true;
    }

    @Override
    public boolean usesFaceDirection() {
        return true;
    }


}
