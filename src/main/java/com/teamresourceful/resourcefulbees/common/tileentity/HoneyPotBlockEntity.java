package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyPotContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPotBlockEntity extends BlockEntity implements ISyncableGUI {

    private final HoneyPotFluidTank tank = new HoneyPotFluidTank();
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    public HoneyPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_POT_TILE_ENTITY.get(), pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level == null) return null;
        return new HoneyPotContainer(id, level, worldPosition, playerInventory);
    }

    @Override
    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), serverPlayer);
        }
    }

    @Override
    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        this.tank.setFluid(buffer.readFluidStack());
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TranslationConstants.Guis.POT;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        nbt.put(NBTConstants.NBT_TANK, getTank().writeToNBT(new CompoundTag()));
        return super.save(nbt);
    }

    //TODO switch this to the new NBT system
    /*@Override
    public void load(@NotNull BlockState state, CompoundTag nbt) {
        getTank().readFromNBT(nbt.getCompound(NBTConstants.NBT_TANK));
        super.load(state, nbt);
    }*/

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
