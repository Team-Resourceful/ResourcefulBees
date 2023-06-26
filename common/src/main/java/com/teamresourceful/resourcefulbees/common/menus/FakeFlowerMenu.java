package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.FakeFlowerBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.base.ContainerSlot;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FakeFlowerMenu extends AbstractModContainerMenu<FakeFlowerBlockEntity> {

    public FakeFlowerMenu(int id, Inventory inv, Optional<PositionContent> content) {
        this(id, inv, PositionContent.getOrNull(content, inv.player.level(), FakeFlowerBlockEntity.class));
    }
    public FakeFlowerMenu(int id, Inventory inv, FakeFlowerBlockEntity entity) {
        super(ModMenuTypes.FAKE_FLOWER.get(), id, inv, entity);
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
