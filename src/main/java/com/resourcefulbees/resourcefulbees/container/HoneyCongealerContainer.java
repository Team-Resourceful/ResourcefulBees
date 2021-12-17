package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyCongealerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class HoneyCongealerContainer extends ContainerWithStackMove {

    private final HoneyCongealerTileEntity honeyCongealerTileEntity;
    private final PlayerEntity player;

    public HoneyCongealerContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.HONEY_CONGEALER_CONTAINER.get(), id);

        this.player = inv.player;
        honeyCongealerTileEntity = (HoneyCongealerTileEntity) world.getBlockEntity(pos);

        if (getHoneyCongealerTileEntity() != null) {
            this.addSlot(new OutputSlot(getHoneyCongealerTileEntity().getTileStackHandler(), HoneyCongealerTileEntity.BOTTLE_OUTPUT, 93, 54));

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
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getHoneyCongealerTileEntity() == null) {
            return;
        }

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getHoneyCongealerTileEntity().sendGUINetworkPacket(listener);
        }
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return true;
    }

    public HoneyCongealerTileEntity getHoneyCongealerTileEntity() {
        return honeyCongealerTileEntity;
    }
}
