package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class BeeLocatorTranslations {

    private BeeLocatorTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @Translate("Cancel")
    public static final MutableComponent CANCEL = Component.translatable("gui.resourcefulbees.bee_locator.cancel");

    @Translate("Search")
    public static final MutableComponent SEARCH = Component.translatable("gui.resourcefulbees.bee_locator.search");

    @Translate("None")
    public static final MutableComponent NONE = Component.translatable("gui.resourcefulbees.bee_locator.none");

    @Translate("Selected: %s")
    public static final String SELECTED = "gui.resourcefulbees.bee_locator.selected";

    @Translate("Loc: %s / %s")
    public static final String LOCATION = "gui.resourcefulbees.bee_locator.location";

    @Translate("Biome: %s")
    public static final String BIOME = "gui.resourcefulbees.bee_locator.biome";

    @Translate("Dist: %sm")
    public static final String DISTANCE = "gui.resourcefulbees.bee_locator.distance";
}
