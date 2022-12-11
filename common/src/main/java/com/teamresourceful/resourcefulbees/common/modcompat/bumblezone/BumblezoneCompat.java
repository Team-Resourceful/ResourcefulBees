package com.teamresourceful.resourcefulbees.common.modcompat.bumblezone;

import com.teamresourceful.resourcefulbees.common.modcompat.base.ModCompat;
import com.telepathicgrunt.the_bumblezone.modcompat.BumblezoneAPI;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class BumblezoneCompat implements ModCompat {

    @Override
    public boolean shouldNotAngerBees(ServerPlayer player) {
        return BumblezoneAPI.playerHasBeeEssence(player);
    }

    @Override
    public IntDoublePair rollExtraHoneycombs(Player player, boolean scraper) {
        if (player instanceof ServerPlayer serverPlayer) {
            int level = BumblezoneAPI.getCombCutterLevelForItem(serverPlayer);
            if (level > 0) {
                double chance = scraper ? 0.2 : 0.5;
                chance += (level * 0.125) - 0.125;
                BumblezoneAPI.triggerCombCutterExtraDropAdvancement(serverPlayer);
                return IntDoublePair.of(level * 3, chance);
            }
        }
        return ModCompat.super.rollExtraHoneycombs(player, scraper);
    }

}
