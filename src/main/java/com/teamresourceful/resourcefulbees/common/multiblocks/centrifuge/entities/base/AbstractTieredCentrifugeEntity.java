package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.tileentity.ISyncableGUI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;

import javax.annotation.Nonnull;

public abstract class AbstractTieredCentrifugeEntity extends AbstractCentrifugeEntity implements ISyncableGUI {

    protected final CentrifugeTier tier;

    protected AbstractTieredCentrifugeEntity(TileEntityType<?> tileEntityTypeIn, CentrifugeTier tier) {
        super(tileEntityTypeIn);
        this.tier = tier;
    }

    public CentrifugeTier getTier() {
        return tier;
    }

    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        assert level != null;
        if (Boolean.TRUE.equals(level.getBlockState(worldPosition).getValue(MultiblockBlock.ASSEMBLED))) {
            if (!level.isClientSide) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> {
                    buf.writeBlockPos(this.worldPosition);
                    buf.writeRegistryId(this.getType());
                });
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }

    @Override
    public void sendGUINetworkPacket(IContainerListener player) {
    }

    @Override
    public void handleGUINetworkPacket(PacketBuffer buffer) {
    }
}
