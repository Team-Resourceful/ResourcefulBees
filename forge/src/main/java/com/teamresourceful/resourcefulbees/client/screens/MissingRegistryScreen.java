package com.teamresourceful.resourcefulbees.client.screens;

import com.teamresourceful.resourcefulbees.common.init.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;

public class MissingRegistryScreen extends ConfirmScreen {

    private static boolean hasShown = false;

    private MissingRegistryScreen(Screen parent) {
        super(proceeded -> {
                if (proceeded) {
                    Minecraft.getInstance().setScreen(parent);
                } else {
                    Minecraft.getInstance().stop();
                }
            },
            TranslationConstants.MissingRegistry.TITLE,
            TranslationConstants.MissingRegistry.DESCRIPTION,
            TranslationConstants.MissingRegistry.PROCEED,
            TranslationConstants.MissingRegistry.QUIT
        );
        setDelay(30);
    }

    public static void onScreenChange(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen && MissingRegistrySetup.isMissingRegistries()) {
            if (!hasShown) {
                hasShown = true;
                event.setNewScreen(new MissingRegistryScreen(event.getNewScreen()));
            }
        }
    }
}
