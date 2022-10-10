package com.teamresourceful.resourcefulbees.common.capabilities.base;

import net.minecraft.world.entity.player.Player;

public interface SyncableCapability {

    void onSynced(Player player);
}
