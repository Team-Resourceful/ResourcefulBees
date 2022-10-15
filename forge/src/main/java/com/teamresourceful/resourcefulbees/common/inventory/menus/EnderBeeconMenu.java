package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
    public int getPlayerInvXOffset() {
        return 36;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 118;
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
