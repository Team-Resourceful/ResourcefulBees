package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class EnderBeeconMenu extends AbstractModContainerMenu<EnderBeeconBlockEntity> {

    public EnderBeeconMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, EnderBeeconBlockEntity.class));
    }

    public EnderBeeconMenu(int id, Inventory inv, EnderBeeconBlockEntity entity) {
        super(ModMenus.ENDER_BEECON_CONTAINER.get(), id, inv, entity);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public int getInventoryStart() {
        return 2;
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
                this.addSlot(new Slot(inv, j + i * 9 + 9, 36 + j * 18, 118 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 36 + k * 18, 176));
        }
    }

    @Override
    protected void addMenuSlots() {
        //nothing
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    public int getFluid() {
        return entity.getTank().getFluidAmount();
    }

    public Player getPlayer() {
        return player;
    }
}
