package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.LockBeeMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class ApiaryMenu extends AbstractModContainerMenu<ApiaryBlockEntity> {

    public ApiaryMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, ApiaryBlockEntity.class));
    }

    public ApiaryMenu(int id, Inventory inv, ApiaryBlockEntity entity) {
        super(ModMenus.VALIDATED_APIARY_CONTAINER.get(), id, inv, entity);
    }

    @Override
    protected void addMenuSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new OutputSlot(entity.getInventory(), j + i * 9, 56 + j * 18, 18 + i * 18));
            }
        }
    }

    @Override
    protected void addPlayerInvSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 56 + j * 18, 86 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 56 + k * 18, 144));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Override
    public int getContainerInputEnd() {
        return 27;
    }

    @Override
    public int getInventoryStart() {
        return 27;
    }

    @Override
    public int getContainerInputStart() {
        return 27;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 0;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 0;
    }

    public boolean lockOrUnlockBee(int id) {
        if (id >= 0 && id < entity.getBeeCount()) {
            NetPacketHandler.sendToServer(new LockBeeMessage(entity.getBlockPos(), id));
        }
        return true;
    }

    public ApiaryBlockEntity.ApiaryBee getApiaryBee(int i) {
        return entity.bees.get(i);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) entity.sendToPlayer(serverPlayer);
    }
}
