package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class GuiTranslations {

    private GuiTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final MutableComponent INVENTORY = Component.translatable("container.inventory");

    @Translate("Ender Beecon")
    public static final MutableComponent BEECON = Component.translatable("gui.resourcefulbees.ender_beecon");

    @Translate("Solidification Chamber")
    public static final MutableComponent SOLIDIFICATION_CHAMBER = Component.translatable("gui.resourcefulbees.solidification_chamber");

    @Translate("Honey Generator")
    public static final MutableComponent GENERATOR = Component.translatable("gui.resourcefulbees.honey_generator");

    @Translate("Honey Pot")
    public static final MutableComponent POT = Component.translatable("gui.resourcefulbees.honey_pot");

    @Translate("Apiary")
    public static final MutableComponent APIARY = Component.translatable("gui.resourcefulbees.apiary");

    @Translate("Apiary Breeder")
    public static final MutableComponent APIARY_BREEDER = Component.translatable("gui.resourcefulbees.apiary_breeder");

    @Translate("Empty")
    public static final MutableComponent NO_FLUID = Component.translatable("gui.resourcefulbees.fluids.empty");

    @Translate("Sorting by having %s")
    public static final String SORT_TRUE = "gui.resourcefulbees.component.sort_true";

    @Translate("Sorting by not having %s")
    public static final String SORT_FALSE = "gui.resourcefulbees.component.sort_false";

    @Translate("Not Sorting by %s")
    public static final String SORT_UNSET = "gui.resourcefulbees.component.sort_unset";

    @Translate("Sort by %s")
    public static final String SORT_BY = "gui.resourcefulbees.component.sort_by";
}
