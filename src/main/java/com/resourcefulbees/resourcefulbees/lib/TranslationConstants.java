package com.resourcefulbees.resourcefulbees.lib;

import net.minecraft.util.text.TranslationTextComponent;

public class TranslationConstants {

    public static class Items {

        public static final TranslationTextComponent BEECON_TOOLTIP = new TranslationTextComponent("block.resourcefulbees.beecon.tooltip.info");

        public static final TranslationTextComponent BEECON_TOOLTIP_1 = new TranslationTextComponent("block.resourcefulbees.beecon.tooltip.info.1");
    }

    public static class Guis {

        public static final TranslationTextComponent BEECON = new TranslationTextComponent("gui.resourcefulbees.ender_beecon");
        public static class EnderBeecon {

            public static final TranslationTextComponent PRIMARY_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.primary");

            public static final TranslationTextComponent DRAIN_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.drain");

            public static final TranslationTextComponent RANGE_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.range");

            public static final TranslationTextComponent ACTIVE_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.is_active");

            public static final TranslationTextComponent FLUID_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.fluid");

            public static final TranslationTextComponent FLUID_AMOUNT_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.fluid_amount");

            public static final TranslationTextComponent NO_FLUID_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.no_fluid");
        }
    }
}
