package com.teamresourceful.resourcefulbees.common.compat.base;

import com.teamresourceful.resourcefulbees.common.compat.bumblezone.BumblezoneCompat;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModCompatHelper {

    private static final List<ModCompat> COMPATS = new ArrayList<>();

    public static void registerCompats() {
        if (ModList.get().isLoaded("the_bumblezone")) {
            COMPATS.add(new BumblezoneCompat());
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
