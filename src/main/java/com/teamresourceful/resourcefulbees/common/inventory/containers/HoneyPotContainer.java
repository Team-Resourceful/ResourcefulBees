package com.teamresourceful.resourcefulbees.common.inventory.containers;

import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyPotTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HoneyPotContainer extends ContainerWithStackMove {

    private final HoneyPotTileEntity tileEntity;

    public HoneyPotContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.HONEY_POT_CONTAINER.get(), id);

        this.tileEntity = (HoneyPotTileEntity) world.getBlockEntity(pos);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }
    }

    public HoneyPotTileEntity getTileEntity() {
        return tileEntity;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    public int getContainerInputEnd() {
        return 26;
    }

    @Override
    public int getInventoryStart() {
        return 27;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getTileEntity() == null) return;

        for (ContainerListener listener : ((ContainerAccessor) this).getListeners())
            getTileEntity().sendGUINetworkPacket(listener);
    }

}
