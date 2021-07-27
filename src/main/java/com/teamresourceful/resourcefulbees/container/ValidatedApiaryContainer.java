package com.teamresourceful.resourcefulbees.container;

import com.teamresourceful.resourcefulbees.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.network.packets.LockBeeMessage;
import com.teamresourceful.resourcefulbees.registry.ModContainers;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ValidatedApiaryContainer extends ContainerWithStackMove {

    private final IntReferenceHolder selectedBee = IntReferenceHolder.standalone();
    private final ApiaryTileEntity apiaryTileEntity;
    private final BlockPos pos;
    private final PlayerEntity player;
    private String[] beeList;

    public ValidatedApiaryContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.VALIDATED_APIARY_CONTAINER.get(), id);

        this.player = inv.player;
        this.pos = pos;
        this.apiaryTileEntity = (ApiaryTileEntity) world.getBlockEntity(pos);

        if (getApiaryTileEntity() != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(getApiaryTileEntity().getTileStackHandler(), ApiaryTileEntity.IMPORT, 74, 37) {
                @Override
                public int getMaxStackSize() {
                    return getApiaryTileEntity().getTileStackHandler().getSlotLimit(ApiaryTileEntity.IMPORT);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getApiaryTileEntity().getTileStackHandler().isItemValid(ApiaryTileEntity.IMPORT, stack);
                }
            });
            this.addSlot(new SlotItemHandlerUnconditioned(getApiaryTileEntity().getTileStackHandler(), ApiaryTileEntity.EMPTY_JAR, 128, 37) {
                @Override
                public int getMaxStackSize() {
                    return getApiaryTileEntity().getTileStackHandler().getSlotLimit(ApiaryTileEntity.EMPTY_JAR);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getApiaryTileEntity().getTileStackHandler().isItemValid(ApiaryTileEntity.EMPTY_JAR, stack);
                }
            });
            this.addSlot(new OutputSlot(getApiaryTileEntity().getTileStackHandler(), ApiaryTileEntity.EXPORT, 182, 37));
            if (!world.isClientSide) {
                this.getApiaryTileEntity().setNumPlayersUsing(this.getApiaryTileEntity().getNumPlayersUsing() + 1);
                ApiaryTileEntity.syncApiaryToPlayersUsing(world, pos, this.getApiaryTileEntity().saveToNBT(new CompoundNBT()));
            }
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 56 + j * 18, 70 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 56 + k * 18, 128));
        }
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void removed(@NotNull PlayerEntity playerIn) {
        World world = this.getApiaryTileEntity().getLevel();
        if (world != null && !world.isClientSide)
            this.getApiaryTileEntity().setNumPlayersUsing(this.getApiaryTileEntity().getNumPlayersUsing() - 1);
        super.removed(playerIn);
    }

    @Override
    public int getContainerInputEnd() {
        return 2;
    }

    @Override
    public int getInventoryStart() {
        return 3;
    }

    public boolean selectBee(int id) {
        if (id >= -1 && id < getApiaryTileEntity().getBeeCount()) {
            this.selectedBee.set(id);
        }
        return true;
    }

    public boolean lockOrUnlockBee(int id) {
        if (id >= 0 && id < getApiaryTileEntity().getBeeCount()) {
            NetPacketHandler.sendToServer(new LockBeeMessage(getApiaryTileEntity().getBlockPos(), getBeeList()[id]));
        }
        return true;
    }

    public int getSelectedBee() { return this.selectedBee.get(); }

    public ApiaryTileEntity.ApiaryBee getApiaryBee(int i) {
        return getApiaryTileEntity().bees.get(getBeeList()[i]);
    }

    public ApiaryTileEntity getApiaryTileEntity() {
        return apiaryTileEntity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public String[] getBeeList() {
        return beeList;
    }

    public void setBeeList(String[] beeList) {
        this.beeList = beeList;
    }
}
