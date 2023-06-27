package com.teamresourceful.resourcefulbees.common.modcompat.base;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.modcompat.bumblezone.BumblezoneCompat;
import com.teamresourceful.resourcefulbees.common.modcompat.productivebees.ProductiveBeesCompat;
import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ModCompatHelper {

    private static final List<ModCompat> COMPATS = new ArrayList<>();

    private ModCompatHelper() {
        throw new UtilityClassError();
    }

    public static void registerCompats() {
        if (ModInfoUtils.isModLoaded("the_bumblezone")) {
            COMPATS.add(new BumblezoneCompat());
        }
        if (ModInfoUtils.isModLoaded("productivebees")) {
            COMPATS.add(new ProductiveBeesCompat());
        }
    }

    public static boolean shouldNotAngerBees(ServerPlayer player) {
        return !COMPATS.isEmpty() && COMPATS.stream().anyMatch(compat -> compat.shouldNotAngerBees(player));
    }

    public static IntDoublePair rollExtraHoneycombs(@Nullable Player player, boolean scraper) {
        if (player == null) return ModCompat.NO_ROLL;
        int validCompats = 0;
        int rolls = 0;
        double chance = 0;
        for (ModCompat compat : COMPATS) {
            IntDoublePair pair = compat.rollExtraHoneycombs(player, scraper);
            if (pair.firstInt() != 0 && pair.secondDouble() != 0) {
                validCompats++;
                rolls += pair.firstInt();
                chance += pair.secondDouble();
            }
        }
        if (validCompats <= 0) return ModCompat.NO_ROLL;
        return IntDoublePair.of(rolls / validCompats, chance / validCompats);
    }
}
