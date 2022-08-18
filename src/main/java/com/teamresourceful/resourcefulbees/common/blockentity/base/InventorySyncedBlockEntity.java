package com.teamresourceful.resourcefulbees.common.blockentity.base;

import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventorySyncedBlockEntity extends GUISyncedBlockEntity {

    private final AutomationSensitiveItemStackHandler inventory = createInventory();
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getInventory);

    protected InventorySyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) return inventoryOptional.cast();
        return super.getCapability(cap, side);
    }

    public abstract AutomationSensitiveItemStackHandler createInventory();

    public @NotNull AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
    }

}
