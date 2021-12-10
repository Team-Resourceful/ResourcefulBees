package com.teamresourceful.resourcefulbees.common.inventory.containers;

import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.inventory.slots.SlotItemHandlerUnconditioned;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.LockBeeMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ValidatedApiaryContainer extends ContainerWithStackMove {

    private final DataSlot selectedBee = DataSlot.standalone();
    private final ApiaryTileEntity apiaryTileEntity;
    private final BlockPos pos;
    private final Player player;
    private String[] beeList;

    public ValidatedApiaryContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.VALIDATED_APIARY_CONTAINER.get(), id);

        this.player = inv.player;
        this.pos = pos;
        this.apiaryTileEntity = (ApiaryTileEntity) world.getBlockEntity(pos);

        if (apiaryTileEntity != null) {
            addInputSlot(apiaryTileEntity, ApiaryTileEntity.IMPORT, 74);
            addInputSlot(getApiaryTileEntity(), ApiaryTileEntity.EMPTY_JAR, 128);
            this.addSlot(new OutputSlot(apiaryTileEntity.getTileStackHandler(), ApiaryTileEntity.EXPORT, 182, 37));
            if (!world.isClientSide) apiaryTileEntity.syncApiaryToPlayersUsing();
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

    private void addInputSlot(ApiaryTileEntity apiaryTileEntity, int slot, int xPos) {
        this.addSlot(new SlotItemHandlerUnconditioned(apiaryTileEntity.getTileStackHandler(), slot, xPos, 37) {
            @Override
            public int getMaxStackSize() {
                return apiaryTileEntity.getTileStackHandler().getSlotLimit(slot);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return apiaryTileEntity.getTileStackHandler().isItemValid(slot, stack);
            }
        });
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    //TODO is this an artifact left over from the unstash? - oreo
/*    @Override
    public void removed(@NotNull PlayerEntity playerIn) {
        World world = this.getApiaryTileEntity().getLevel();
        if (world != null && !world.isClientSide)
            this.getApiaryTileEntity().setNumPlayersUsing(this.getApiaryTileEntity().getNumPlayersUsing() - 1);
        super.removed(playerIn);
    }*/

    @Override
    public int getContainerInputEnd() {
        return 2;
    }

    @Override
    public int getInventoryStart() {
        return 3;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
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

    public Player getPlayer() {
        return player;
    }

    public String[] getBeeList() {
        return beeList;
    }

    public void setBeeList(String[] beeList) {
        this.beeList = beeList;
    }

    @Override
    public void addSlotListener(@NotNull ContainerListener listener) {
        super.addSlotListener(listener);
        apiaryTileEntity.setListeners(((ContainerAccessor) this).getListeners());
        //apiaryTileEntity.setNumPlayersUsing(((ContainerAccessor) this).getListeners().size());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void removeSlotListener(@NotNull ContainerListener listener) {
        super.removeSlotListener(listener);
        apiaryTileEntity.setListeners(((ContainerAccessor) this).getListeners());
        //apiaryTileEntity.setNumPlayersUsing(((ContainerAccessor) this).getListeners().size());
    }
}
