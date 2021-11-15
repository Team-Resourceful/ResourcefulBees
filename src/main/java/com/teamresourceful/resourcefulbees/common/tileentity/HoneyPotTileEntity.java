package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyPotContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPotTileEntity extends TileEntity implements ISyncableGUI {

    private final HoneyPotFluidTank tank = new HoneyPotFluidTank();
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    public HoneyPotTileEntity() {
        super(ModBlockEntityTypes.HONEY_POT_TILE_ENTITY.get());
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        if (level == null) return null;
        return new HoneyPotContainer(id, level, worldPosition, playerInventory);
    }

    @Override
    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    @Override
    public void handleGUINetworkPacket(PacketBuffer buffer) {
        this.tank.setFluid(buffer.readFluidStack());
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return TranslationConstants.Guis.POT;
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT nbt) {
        nbt.put(NBTConstants.NBT_TANK, getTank().writeToNBT(new CompoundNBT()));
        return super.save(nbt);
    }

    @Override
    public void load(@NotNull BlockState state, CompoundNBT nbt) {
        getTank().readFromNBT(nbt.getCompound(NBTConstants.NBT_TANK));
        super.load(state, nbt);
    }

    public HoneyPotFluidTank getTank() {
        return this.tank;
    }

    public static class HoneyPotFluidTank extends HoneyFluidTank {

        public HoneyPotFluidTank() {
            super(64000);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int filled = super.fill(resource, action);
            return getFluid().isFluidEqual(resource) ? resource.getAmount() : filled;
        }

    }

}
