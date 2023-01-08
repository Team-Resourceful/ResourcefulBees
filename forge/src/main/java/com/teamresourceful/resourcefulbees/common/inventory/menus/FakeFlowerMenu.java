package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.FakeFlowerEntity;
import com.teamresourceful.resourcefulbees.common.inventory.slots.OutputSlot;
import com.teamresourceful.resourcefulbees.common.menus.AbstractModContainerMenu;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FakeFlowerMenu extends AbstractModContainerMenu<FakeFlowerEntity> {

    public FakeFlowerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, FakeFlowerEntity.class));
    }
    public FakeFlowerMenu(int id, Inventory inv, FakeFlowerEntity entity) {
        super(ModMenus.FAKE_FLOWER_CONTAINER.get(), id, inv, entity);
    }

    @Override
    protected int getContainerInputEnd() {
        return 5;
    }

    @Override
    protected int getInventoryStart() {
        return 1;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 8;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 51;
    }

    @Override
    protected void addMenuSlots() {
        for (int i = 0; i < entity.getInventory().getSlots(); i++) {
            this.addSlot(new OutputSlot(entity.getInventory(), i, 44 + (i*18), 20));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
