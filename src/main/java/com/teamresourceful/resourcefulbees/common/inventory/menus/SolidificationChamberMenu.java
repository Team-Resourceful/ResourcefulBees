package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SolidificationChamberMenu extends AbstractModContainerMenu<SolidificationChamberBlockEntity> {

    public SolidificationChamberMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, SolidificationChamberBlockEntity.class));
    }

    public SolidificationChamberMenu(int id, Inventory inv, SolidificationChamberBlockEntity entity) {
        super(ModMenus.SOLIDIFICATION_CHAMBER_CONTAINER.get(), id, inv, entity);
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 1;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 84;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 142;
    }

    @Override
    protected void addMenuSlots() {
        this.addSlot(new OutputSlot(entity.getInventory(), SolidificationChamberBlockEntity.BLOCK_OUTPUT, 93, 54));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
