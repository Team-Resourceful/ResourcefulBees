package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.blockentity.base.SyncableGUI;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.phosphophyllite.multiblock2.validated.IValidatedMultiblock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGUICentrifugeEntity extends AbstractTieredCentrifugeEntity implements Nameable, SyncableGUI {

    private final List<ServerPlayer> listeners = new ArrayList<>(); //TODO consider eliminating this

    private Component name;
    private String owner = "null";

    protected final CentrifugeState centrifugeState = new CentrifugeState();


    protected AbstractGUICentrifugeEntity(BlockEntityType<?> tileEntityTypeIn, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, tier, pos, state);
    }

    @Override
    public final BlockPos getBlkPos() {
        return this.getBlockPos();
    }

    @Override
    public final @Nullable Level getLvl() {
        return this.getLevel();
    }

    public CentrifugeState getCentrifugeState() {
        return centrifugeState;
    }

    public void getOpenGUIPacket(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getBlockPos());
        CentrifugeController controller = nullableController();
        if (controller != null && controller.assemblyState() == IValidatedMultiblock.AssemblyState.ASSEMBLED) {
            controller.updateCentrifugeState(centrifugeState);
            centrifugeState.setOwner(owner);
            centrifugeState.serializeBytes(buffer);
        }
    }

    //region NBT Handling
    //on Block Update
    @Nullable
    @Override
    protected CompoundTag getUpdateNBT() {
        return writeNBT();
    }

    @Override
    protected void handleUpdateNBT(@NotNull CompoundTag nbt) {
        readNBT(nbt);
    }

    @Override
    protected void readNBT(@NotNull CompoundTag compound) {
        this.owner = compound.getString("owner");
        super.readNBT(compound);
    }

    @Override
    protected @NotNull CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.putString("owner", this.owner);
        return tag;
    }
    //endregion

    //region GUI Packet

    @Override
    public final List<ServerPlayer> getListeners() {
        return listeners;
    }

    @Override
    public CompoundTag getSyncData() {
        return new CompoundTag();
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
    }
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
