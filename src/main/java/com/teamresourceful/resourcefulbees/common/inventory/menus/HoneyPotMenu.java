package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.HoneyPotBlockEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class HoneyPotMenu extends AbstractModContainerMenu<HoneyPotBlockEntity> {

    public HoneyPotMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, HoneyPotBlockEntity.class));
    }

    public HoneyPotMenu(int id, Inventory inv, HoneyPotBlockEntity entity) {
        super(ModMenus.HONEY_POT_CONTAINER.get(), id, inv, entity);
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 0;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 0;
    }

    @Override
    protected void addPlayerInvSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }
    }

    @Override
    protected void addMenuSlots() {
        //nothing
    }

    @Override
    public int getContainerInputEnd() {
        return 26;
    }

    @Override
    public int getInventoryStart() {
        return 27;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) entity.sendToPlayer(serverPlayer);
    }
}
