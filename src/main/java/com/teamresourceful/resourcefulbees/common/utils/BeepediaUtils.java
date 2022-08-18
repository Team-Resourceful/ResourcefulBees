package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.capabilities.ModCapabilities;
import com.teamresourceful.resourcefulbees.common.capabilities.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.capabilities.beepedia.CreativeBeepediaData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefullib.client.screens.state.ScreenStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public final class BeepediaUtils {

    private BeepediaUtils() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static void loadBeepedia(ItemStack itemstack, Player player) {
        BeepediaData data = player.getCapability(ModCapabilities.BEEPEDIA_DATA).resolve().orElse(null);
        if (itemstack.hasTag() && itemstack.getTag() != null && !itemstack.isEmpty() && itemstack.getTag().getBoolean(NBTConstants.Beepedia.CREATIVE)) {
            data = CreativeBeepediaData.INSTANCE;
        }
        Screen screen = ScreenStateManager.getScreen(BeepediaScreen.STATE_ID, BeepediaScreen::new);
        if (screen instanceof BeepediaScreen beepediaScreen) beepediaScreen.updateData(data);
        Minecraft.getInstance().setScreen(screen);
    }

    public static void capabilityUpdated(BeepediaData data) {
        if (Minecraft.getInstance().screen instanceof BeepediaScreen beepedia) {
            beepedia.updateData(data);
        }
    }
}
