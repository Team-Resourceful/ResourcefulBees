package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.ISyncableGUI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public interface ListeningMenu {

    @NotNull
    ISyncableGUI getSyncedBlockEntity();

    @NotNull
    Player getSyncedPlayer();

    /**
     * This is an interface to allow for better compatability with custom menu classes.
     * So this method should be called on {@link AbstractContainerMenu} constructor.
     */
    default void addListener() {
        if (getSyncedPlayer() instanceof ServerPlayer serverPlayer) {
            getSyncedBlockEntity().addListeningPlayer(serverPlayer);
        }
    }

    /**
     * This is an interface to allow for better compatability with custom menu classes.
     * So this method should be called on
     * {@link AbstractContainerMenu#removed(Player)}
     */
    default void removeListener() {
        if (getSyncedPlayer() instanceof ServerPlayer serverPlayer) {
            getSyncedBlockEntity().removeListeningPlayer(serverPlayer);
        }
    }
}
