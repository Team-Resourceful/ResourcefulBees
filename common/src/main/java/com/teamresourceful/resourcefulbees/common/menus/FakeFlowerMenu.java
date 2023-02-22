package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.FakeFlowerBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.base.ContainerSlot;
import com.teamresourceful.resourcefulbees.platform.common.util.TempPlatformUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FakeFlowerMenu extends AbstractModContainerMenu<FakeFlowerBlockEntity> {

    public FakeFlowerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, FakeFlowerBlockEntity.class));
    }
    public FakeFlowerMenu(int id, Inventory inv, FakeFlowerBlockEntity entity) {
        super(TempPlatformUtils.getFakeFlowerMenuType().get(), id, inv, entity);
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
        for (int i = 0; i < entity.getContainerSize(); i++) {
            this.addSlot(new ContainerSlot(entity, i, 44 + (i*18), 20));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
