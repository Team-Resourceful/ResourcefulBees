package com.teamresourceful.resourcefulbees.common.compat.bumblezone;

import com.teamresourceful.resourcefulbees.common.compat.base.ModCompat;
import com.telepathicgrunt.the_bumblezone.modcompat.BumblezoneAPI;
import net.minecraft.server.level.ServerPlayer;

public class BumblezoneCompat implements ModCompat {

    @Override
    public boolean shouldNotAngerBees(ServerPlayer player) {
        return BumblezoneAPI.playerHasBeeEssence(player);
    }
}
