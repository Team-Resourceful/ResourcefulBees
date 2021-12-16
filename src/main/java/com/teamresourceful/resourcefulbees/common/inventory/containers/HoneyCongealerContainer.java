package com.teamresourceful.resourcefulbees.common.inventory.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.SolidificationChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HoneyCongealerContainer extends ContainerWithStackMove {

    private final SolidificationChamberBlockEntity honeyCongealerTileEntity;

    public HoneyCongealerContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.SOLIDIFICATION_CHAMBER_CONTAINER.get(), id);

        honeyCongealerTileEntity = (SolidificationChamberBlockEntity) world.getBlockEntity(pos);

        if (getHoneyCongealerTileEntity() != null) {
            this.addSlot(new OutputSlot(getHoneyCongealerTileEntity().getInventory(), SolidificationChamberBlockEntity.BLOCK_OUTPUT, 93, 54));

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
            }
        }
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 1;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getHoneyCongealerTileEntity() == null) return;

        for (ContainerListener listener : ((ContainerAccessor) this).getListeners()) getHoneyCongealerTileEntity().sendGUINetworkPacket(listener);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public SolidificationChamberBlockEntity getHoneyCongealerTileEntity() {
        return honeyCongealerTileEntity;
    }
}
