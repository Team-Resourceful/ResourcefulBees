package com.teamresourceful.resourcefulbees.common.modcompat.resourcefulbees;

import com.teamresourceful.resourcefulbees.common.modcompat.base.ModCompat;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import it.unimi.dsi.fastutil.ints.IntDoubleImmutablePair;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.world.entity.player.Player;

public record ResourcefulBeesCompat() implements ModCompat {

    @Override
    public IntDoublePair rollExtraHoneycombs(Player player, boolean scraper) {
        if (player.hasEffect(ModEffects.BEEKEEPERS_RESOLVE.holder())) {
            return new IntDoubleImmutablePair(1, 0.20d);
        }

        return ModCompat.super.rollExtraHoneycombs(player, scraper);
    }
}
