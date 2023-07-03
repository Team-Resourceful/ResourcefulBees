package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.HoneyPotBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HoneyPotMenu extends AbstractModContainerMenu<HoneyPotBlockEntity> {

    public HoneyPotMenu(int id, Inventory inv,  Optional<PositionContent> content) {
        this(id, inv, PositionContent.getOrNull(content, inv.player.level(), HoneyPotBlockEntity.class));
    }

    public HoneyPotMenu(int id, Inventory inv, HoneyPotBlockEntity entity) {
        super(ModMenuTypes.HONEY_POT.get(), id, inv, entity);
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
