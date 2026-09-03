package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.lib.constants.translations.MissingRegistryTranslations;
import com.teamresourceful.resourcefulbees.common.setup.MissingRegistrySetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class MissingRegistryScreen extends ConfirmScreen {

    private static boolean hasShown = false;

    private MissingRegistryScreen(Screen parent) {
        super(
                proceeded -> {
                    Minecraft minecraft = Minecraft.getInstance();

                    if (proceeded) {
                        minecraft.gui.setScreen(parent);
                    } else {
                        minecraft.stop();
                    }
                },
                MissingRegistryTranslations.TITLE,
                MissingRegistryTranslations.DESCRIPTION,
                MissingRegistryTranslations.PROCEED,
                MissingRegistryTranslations.QUIT
        );
    }

    @Override
    protected void init() {
        super.init();
        setDelay(30);
    }

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof TitleScreen titleScreen)) {
            return;
        }

        if (hasShown) {
            return;
        }

        if (!MissingRegistrySetup.isMissingRegistries()) {
            return;
        }

        hasShown = true;

        event.setNewScreen(new MissingRegistryScreen(titleScreen));
    }
}