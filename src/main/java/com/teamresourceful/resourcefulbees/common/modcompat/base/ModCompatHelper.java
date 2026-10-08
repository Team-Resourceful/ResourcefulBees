package com.teamresourceful.resourcefulbees.common.modcompat.base;

import com.teamresourceful.resourcefulbees.common.modcompat.resourcefulbees.ResourcefulBeesCompat;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ModCompatHelper {

    private static final List<ModCompat> COMPATIBLE_MODS = new ArrayList<>();

    private ModCompatHelper() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void registerCompatibleMods() {
        COMPATIBLE_MODS.add(new ResourcefulBeesCompat());

        if (ModInfoUtils.isModLoaded("the_bumblezone")) {
            //COMPATIBLE_MODS.add(new BumblezoneCompat());
        }
    }

    public static boolean shouldNotAngerBees(ServerPlayer player) {
        return !COMPATIBLE_MODS.isEmpty() && COMPATIBLE_MODS.stream().anyMatch(compat -> compat.shouldNotAngerBees(player));
    }

    public static IntDoublePair rollExtraHoneycombs(@Nullable Player player, boolean scraper) {
        if (player == null) return ModCompat.NO_ROLL;
        int validCompats = 0;
        int rolls = 0;
        double chance = 0;
        for (ModCompat compat : COMPATIBLE_MODS) {
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
