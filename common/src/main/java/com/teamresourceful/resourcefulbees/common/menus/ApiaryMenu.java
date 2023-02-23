package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BlockBee;
import com.teamresourceful.resourcefulbees.common.menus.base.ContainerSlot;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.LockBeePacket;
import com.teamresourceful.resourcefulbees.platform.common.util.TempPlatformUtils;
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
        super(TempPlatformUtils.getApiaryMenuType().get(), id, inv, entity);
    }

    @Override
    protected void addMenuSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new ContainerSlot(entity, j + i * 9, 56 + j * 18, 18 + i * 18));
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
    public int startIndex() {
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
            NetworkHandler.CHANNEL.sendToServer(new LockBeePacket(entity.getBlockPos(), id));
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
