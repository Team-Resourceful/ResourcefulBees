package com.teamresourceful.resourcefulbees.common.compat.bumblezone;

import com.teamresourceful.resourcefulbees.common.compat.base.ModCompat;
import com.telepathicgrunt.the_bumblezone.modcompat.BumblezoneAPI;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEnchantments;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BumblezoneCompat implements ModCompat {

    @Override
    public boolean shouldNotAngerBees(ServerPlayer player) {
        return BumblezoneAPI.playerHasBeeEssence(player);
    }

    @Override
    public IntDoublePair rollExtraHoneycombs(Player player, boolean scraper) {
        //TODO Update to Bumblezone API when it is released
        int level = EnchantmentHelper.getEnchantmentLevel(BzEnchantments.COMB_CUTTER.get(), player);
        if (level > 0) {
            double chance = scraper ? 0.2 : 0.5;
            chance += (level * 0.125) - 0.125;
            if (player instanceof ServerPlayer serverPlayer) {
                BzCriterias.COMB_CUTTER_EXTRA_DROPS_TRIGGER.trigger(serverPlayer);
            }
            return IntDoublePair.of(level * 3, chance);
        }
        return ModCompat.super.rollExtraHoneycombs(player, scraper);
    }

}
