package com.teamresourceful.resourcefulbees.common.compat.base;

import com.teamresourceful.resourcefulbees.common.compat.bumblezone.BumblezoneCompat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

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
}
