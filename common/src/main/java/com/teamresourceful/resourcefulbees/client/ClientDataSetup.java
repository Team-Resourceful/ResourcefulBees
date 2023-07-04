package com.teamresourceful.resourcefulbees.client;

import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.platform.common.events.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

public final class ClientDataSetup {

    private static boolean setupsDone = false;

    public static void onUpdates(UpdateEvent ignored){
        if (Minecraft.getInstance().isLocalServer()) {
            startSetups();
        }
    }

    private static void startSetups() {
        if (!setupsDone) {
            setupsDone = true;

            Level level = Minecraft.getInstance().level;
            RegistryAccess access = level == null ? null : level.registryAccess();
            BeeRegistry.getRegistry().regenerateCustomBeeData(access);
        }
    }
}
