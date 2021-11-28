package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.tileentity.ISyncableGUI;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGUICentrifugeEntity extends AbstractTieredCentrifugeEntity implements INameable, ISyncableGUI {

    private ITextComponent name;
    private String owner = "null";

    protected final CentrifugeState centrifugeState = new CentrifugeState();

    protected AbstractGUICentrifugeEntity(TileEntityType<?> tileEntityTypeIn, CentrifugeTier tier) {
        super(tileEntityTypeIn, tier);
    }

    public CentrifugeState getCentrifugeState() {
        return centrifugeState;
    }

    @Override
    public @NotNull ActionResultType onBlockActivated(@NotNull PlayerEntity player, @NotNull Hand handIn) {
        assert level != null;
        if (Boolean.TRUE.equals(level.getBlockState(worldPosition).getValue(MultiblockBlock.ASSEMBLED))) {
            if (!level.isClientSide) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, this::getOpenGUIPacket);
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(player, handIn);
    }

    protected void getOpenGUIPacket(PacketBuffer buffer) {
        buffer.writeBlockPos(this.worldPosition);
        buffer.writeRegistryId(this.getType());
    }

    //region NBT Handling
    //on Block Update
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, writeNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readNBT(pkt.getTag());
    }

    //on Chunk Load
    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
    }

    //endregion

    //region GUI Packet
    @Override
    public void sendGUINetworkPacket(IContainerListener player) {}

    @Override
    public void handleGUINetworkPacket(PacketBuffer buffer) {}

    @Override
    public final void sendInitGUIPacket(ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            if (controller != null && controller.assemblyState().equals(MultiblockController.AssemblyState.ASSEMBLED)) {
                controller.updateCentrifugeState(centrifugeState);
                centrifugeState.setOwner(owner);
            }
            buffer.writeNbt(centrifugeState.serializeNBT());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer, true), player);
        }
    }

    @Override
    public final void handleInitGUIPacket(PacketBuffer buffer) {
        CompoundNBT nbt = buffer.readNbt();
        if (nbt != null) centrifugeState.deserializeNBT(nbt);
    }
    //endregion


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    //region Custom Name
    public final void setCustomName(ITextComponent pName) {
        this.name = pName;
    }

    @Override
    public final @NotNull ITextComponent getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public final @NotNull ITextComponent getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public final ITextComponent getCustomName() {
        return this.name;
    }

    protected abstract ITextComponent getDefaultName();
    //endregion
}
