package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.blockentity.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.menus.HoneyPotMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPotBlockEntity extends GUISyncedBlockEntity {

    private final HoneyPotFluidTank tank = new HoneyPotFluidTank() {
        @Override
        protected void onContentsChanged() {
            sendToPlayersTrackingChunk();
        }
    };
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    public HoneyPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_POT_TILE_ENTITY.get(), pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.FLUID_HANDLER)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new HoneyPotMenu(id, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TranslationConstants.Guis.POT;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    public HoneyPotFluidTank getTank() {
        return this.tank;
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
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
