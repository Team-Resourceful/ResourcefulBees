package com.teamresourceful.resourcefulbees.common.modcompat.base;

import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface ModCompat {

    IntDoublePair NO_ROLL = IntDoublePair.of(0, 0);

    /**
     * Called when a player is about to anger bees.
     * @param player The player that is about to anger bees.
     * @return True if the player should not anger bees.
     */
    default boolean shouldNotAngerBees(ServerPlayer player) {
        return false;
    }

    /**
     * Determines how many successful rolls and the chance on each roll will be.
     * @param player The player that is rolling for the drops.
     * @param scraper True if the player is using a scraper.
     * @return IntDoublePair of rolls and chance
     */
    default IntDoublePair rollExtraHoneycombs(Player player, boolean scraper) {
        return NO_ROLL;
    }
}
