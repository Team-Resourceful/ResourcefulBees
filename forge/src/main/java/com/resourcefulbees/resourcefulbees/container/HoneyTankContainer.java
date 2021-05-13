package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.AbstractHoneyTankContainer;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HoneyTankContainer extends ContainerWithStackMove {

    private final HoneyTankTileEntity honeyTankTileEntity;

    public HoneyTankContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.HONEY_TANK_CONTAINER.get(), id);

        Player player = inv.player;
        honeyTankTileEntity = (HoneyTankTileEntity) world.getBlockEntity(pos);

        if (getHoneyTankTileEntity() != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(getHoneyTankTileEntity().getTileStackHandler(), AbstractHoneyTankContainer.BOTTLE_INPUT_EMPTY, 53, 16) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return AbstractHoneyTankContainer.isItemValid(stack);
                }
            });
            this.addSlot(new OutputSlot(getHoneyTankTileEntity().getTileStackHandler(), AbstractHoneyTankContainer.BOTTLE_OUTPUT_EMPTY, 53, 54));

            this.addSlot(new SlotItemHandlerUnconditioned(getHoneyTankTileEntity().getTileStackHandler(), HoneyTankTileEntity.BOTTLE_INPUT_FILL, 107, 16) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    boolean isBucket = stack.getItem() == Items.BUCKET;
                    boolean isBottle = stack.getItem() == Items.GLASS_BOTTLE;
                    return isBottle || isBucket;
                }
            });
            this.addSlot(new OutputSlot(getHoneyTankTileEntity().getTileStackHandler(), HoneyTankTileEntity.BOTTLE_OUTPUT_FILL, 107, 54));

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
    public int getInventoryStart() {
        return 4;
    }

    @Override
    public int getContainerInputEnd() {
        return 3;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getHoneyTankTileEntity() == null) {
            return;
        }

        for (ContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getHoneyTankTileEntity().sendGUINetworkPacket(listener);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public HoneyTankTileEntity getHoneyTankTileEntity() {
        return honeyTankTileEntity;
    }

}
