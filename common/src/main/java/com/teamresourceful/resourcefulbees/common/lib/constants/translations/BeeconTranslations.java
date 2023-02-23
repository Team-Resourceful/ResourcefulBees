package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class BeeconTranslations {

    private BeeconTranslations() {
        throw new UtilityClassError();
    }

    @Translate("Bee Effects")
    public static final MutableComponent PRIMARY_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.primary");

    @Translate("Drain: ")
    public static final MutableComponent DRAIN_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.drain");

    @Translate("Range: ")
    public static final MutableComponent RANGE_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.range");

    @Translate("Is Active: ")
    public static final MutableComponent ACTIVE_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.is_active");

    @Translate("Fluid: ")
    public static final MutableComponent FLUID_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.fluid");

    @Translate("Amount: ")
    public static final MutableComponent FLUID_AMOUNT_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.fluid_amount");

    @Translate("Empty")
    public static final MutableComponent NO_FLUID_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.no_fluid");

    @Translate("Beecon Effect Button")
    public static final MutableComponent BEECON_EFFECT_BUTTON = Component.translatable("gui.resourcefulbees.beecon.button.effect");

    @Translate("Active")
    public static final MutableComponent EFFECT_ACTIVE = Component.translatable("gui.resourcefulbees.beecon.effect.active");

    @Translate("Not Active")
    public static final MutableComponent EFFECT_INACTIVE = Component.translatable("gui.resourcefulbees.beecon.effect.inactive");

    @Translate("Range %s")
    public static final String EFFECT_RANGE = "gui.resourcefulbees.beecon.effect.range";
}
