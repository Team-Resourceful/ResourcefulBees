package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class MissingRegistryTranslations {

    private MissingRegistryTranslations() {
        throw new UtilityClassError();
    }

    @Translate("Quit")
    public static final MutableComponent QUIT = Component.translatable("gui.resourcefulbees.missing_registry.quit");

    @Translate("Proceed")
    public static final MutableComponent PROCEED = Component.translatable("gui.resourcefulbees.missing_registry.proceed");

    @Translate("Resourceful Bees custom registry entries missing!")
    public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.missing_registry.title");

    @Translate("Bees, Honey, or Combs in this instance that were loaded previously are missing\nthis startup, are you sure you would you like to proceed?\nInformation on which entries are missing provided in the logs.")
    public static final MutableComponent DESCRIPTION = Component.translatable("gui.resourcefulbees.missing_registry.description");
}
