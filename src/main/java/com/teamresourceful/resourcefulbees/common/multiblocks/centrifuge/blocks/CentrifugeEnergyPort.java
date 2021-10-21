package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeEnergyPortEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeEnergyPort extends AbstractCentrifuge {

    private final RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> tileType;

    public CentrifugeEnergyPort(@NotNull Properties properties, RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> tileType) {
        super(properties);
        this.tileType = tileType;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileType.get().create();
    }

    public enum ConnectionState implements IStringSerializable {
        CONNECTED,
        DISCONNECTED;

        public static final EnumProperty<ConnectionState> CONNECTION_STATE_ENUM_PROPERTY = EnumProperty.create("connection_state", ConnectionState.class);

        @Override
        public @NotNull String getSerializedName() {
            return toString().toLowerCase();
        }
    }

    @Override
    public boolean usesFaceDirection() {
        return true;
    }
}
