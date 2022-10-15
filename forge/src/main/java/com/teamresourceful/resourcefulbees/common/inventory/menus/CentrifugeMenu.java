package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.centrifuge.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.inventory.slots.SlotItemHandlerUnconditioned;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CentrifugeMenu extends AbstractModContainerMenu<CentrifugeBlockEntity> {

    public CentrifugeMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, CentrifugeBlockEntity.class));
    }

    public CentrifugeMenu(int id, Inventory inv, CentrifugeBlockEntity entity) {
        super(ModMenus.CENTRIFUGE_MENU.get(), id, inv, entity);
        if (player instanceof ServerPlayer serverPlayer) {
            entity.sendToPlayer(serverPlayer);
            entity.addListeningPlayer(serverPlayer);
        }
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 13;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 8;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 84;
    }

    @Override
    protected void addMenuSlots() {
        this.addSlot(new SlotItemHandlerUnconditioned(entity.getInventory(), 0, 26, 11));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlot(new OutputSlot(entity.getInventory(), 1 + j + i * 4, 62 + j * 18, 11 + i * 18));
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            this.entity.removeListeningPlayer(serverPlayer);
        }
    }
}
