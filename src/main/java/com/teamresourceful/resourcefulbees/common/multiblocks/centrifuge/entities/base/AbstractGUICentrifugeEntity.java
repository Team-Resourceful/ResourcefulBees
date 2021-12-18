package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.blockentity.ISyncableGUI;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGUICentrifugeEntity extends AbstractTieredCentrifugeEntity implements Nameable, ISyncableGUI {

    private final List<ServerPlayer> listeners = new ArrayList<>();

    private Component name;
    private String owner = "null";

    protected final CentrifugeState centrifugeState = new CentrifugeState();


    protected AbstractGUICentrifugeEntity(BlockEntityType<?> tileEntityTypeIn, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, tier, pos, state);
    }

    public CentrifugeState getCentrifugeState() {
        return centrifugeState;
    }

    public void getOpenGUIPacket(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getBlockPos());
        buffer.writeRegistryId(this.getType());
    }

    //region NBT Handling
    //on Block Update
/*    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> writeNBT());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readNBT(pkt.getTag());
    }

    //on Chunk Load
    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
    }*/

    //endregion

    //region GUI Packet

    @Override
    public final List<ServerPlayer> getListeners() {
        return listeners;
    }

    @Override
    public CompoundTag getSyncData() {
        return null;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {

    }

    /*    @Override
    public void sendGUINetworkPacket(ContainerListener player) {}

    @Override
    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {}

    @Override
    public final void sendInitGUIPacket(ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            controller();
            if (controller().assemblyState().equals(MultiblockController.AssemblyState.ASSEMBLED)) {
                controller().updateCentrifugeState(centrifugeState);
                centrifugeState.setOwner(owner);
            }
            buffer.writeNbt(centrifugeState.serializeNBT());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer, true), player);
        }
    }

    @Override
    public final void handleInitGUIPacket(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        if (nbt != null) centrifugeState.deserializeNBT(nbt);
    }*/
    //endregion


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    //region Custom Name
    public final void setCustomName(Component pName) {
        this.name = pName;
    }

    @Override
    public final @NotNull Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public final @NotNull Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public final Component getCustomName() {
        return this.name;
    }

    protected abstract Component getDefaultName();
    //endregion
}
