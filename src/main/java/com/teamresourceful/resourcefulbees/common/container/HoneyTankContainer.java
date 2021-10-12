package com.teamresourceful.resourcefulbees.common.container;

import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.tileentity.AbstractHoneyTankContainer;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyTankTileEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;


public class HoneyTankContainer extends ContainerWithStackMove {

    private final HoneyTankTileEntity honeyTankTileEntity;

    public HoneyTankContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.HONEY_TANK_CONTAINER.get(), id);

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

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getHoneyTankTileEntity().sendGUINetworkPacket(listener);
        }
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return true;
    }

    public HoneyTankTileEntity getHoneyTankTileEntity() {
        return honeyTankTileEntity;
    }

}
