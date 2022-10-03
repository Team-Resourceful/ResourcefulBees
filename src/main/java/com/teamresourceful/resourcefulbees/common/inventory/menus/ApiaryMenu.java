package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.base.BlockBee;
import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.client.LockBeePacket;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
        return 56;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 86;
    }

    public void lockOrUnlockBee(int id) {
        if (id >= 0 && id < entity.beeCount()) {
            NetPacketHandler.CHANNEL.sendToServer(new LockBeePacket(entity.getBlockPos(), id));
        }
    }

    public BlockBee getApiaryBee(int i) {
        return entity.getBees().get(i);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) entity.sendToPlayer(serverPlayer);
    }
}
