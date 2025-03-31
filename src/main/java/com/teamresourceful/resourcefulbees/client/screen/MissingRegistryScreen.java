package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.lib.constants.translations.MissingRegistryTranslations;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import com.teamresourceful.resourcefulbees.platform.client.events.ScreenOpenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

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
            MissingRegistryTranslations.TITLE,
            MissingRegistryTranslations.DESCRIPTION,
            MissingRegistryTranslations.PROCEED,
            MissingRegistryTranslations.QUIT
        );
        setDelay(30);
    }

    public static void onScreenChange(ScreenOpenEvent event) {
        if (event.getScreen() instanceof TitleScreen && MissingRegistrySetup.isMissingRegistries()) {
            if (!hasShown) {
                hasShown = true;
                event.setScreen(new MissingRegistryScreen(event.getScreen()));
            }
        }
    }
}
