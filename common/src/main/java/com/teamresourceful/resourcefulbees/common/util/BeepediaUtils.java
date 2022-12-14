package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.CreativeBeepediaData;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
import com.teamresourceful.resourcefullib.client.screens.state.ScreenStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public final class BeepediaUtils {

    private BeepediaUtils() {
        throw new UtilityClassError();
    }

    public static void loadBeepedia(ItemStack itemstack, Player player) {
        BeepediaData data = BeepediaSavedData.getBeepediaData(player);
        if (itemstack.hasTag() && itemstack.getTag() != null && !itemstack.isEmpty() && itemstack.getTag().getBoolean(NBTConstants.Beepedia.CREATIVE)) {
            data = CreativeBeepediaData.INSTANCE;
        }
        Screen screen = ScreenStateManager.getScreen(BeepediaScreen.STATE_ID, BeepediaScreen::new);
        if (screen instanceof BeepediaScreen beepediaScreen) beepediaScreen.updateData(data);
        Minecraft.getInstance().setScreen(screen);
    }

    public static void onClientUpdated(BeepediaData data) {
        if (Minecraft.getInstance().screen instanceof BeepediaScreen beepedia) {
            beepedia.updateData(data);
        }
    }
}
