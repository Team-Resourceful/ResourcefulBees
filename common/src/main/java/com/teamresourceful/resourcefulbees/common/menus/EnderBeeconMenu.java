package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnderBeeconMenu extends AbstractModContainerMenu<EnderBeeconBlockEntity> {

    public EnderBeeconMenu(int id, Inventory inv, Optional<PositionContent> content) {
        this(id, inv,PositionContent.getOrNull(content, inv.player.level(), EnderBeeconBlockEntity.class));
    }

    public EnderBeeconMenu(int id, Inventory inv, EnderBeeconBlockEntity entity) {
        super(ModMenuTypes.ENDER_BEECON.get(), id, inv, entity);
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

    public Player getPlayer() {
        return player;
    }
}
