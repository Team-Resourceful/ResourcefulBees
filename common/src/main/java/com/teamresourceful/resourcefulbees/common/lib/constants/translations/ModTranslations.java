package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ModTranslations {

    private ModTranslations() {
        throw new UtilityClassError();
    }

    //Selectable Fluid Widget

    @Translate("Tank Empty")
    public static final MutableComponent TANK_EMPTY = Component.translatable("gui.resourcefulbees.fluid_tank.tooltip.empty");

    @Translate("Hold [CTRL] and [SCROLL] to switch between fluids!")
    public static final MutableComponent FLUID_SWITCH = Component.translatable("gui.resourcefulbees.fluid_tank.tooltip.switch");

    @Translate("Temp Bee Name")
    public static final MutableComponent TEMP_BEE_NAME = Component.translatable("gui.resourcefulbees.bee_holder.temp_name");

    @Translate("Bee template printed to logs!")
    public static final MutableComponent BEE_TEMPLATE_PRINTED = Component.translatable("command.resourcefulbees.template.bee");

    @Translate("Honeycomb template printed to logs!")
    public static final MutableComponent HONEYCOMB_TEMPLATE_PRINTED = Component.translatable("command.resourcefulbees.template.honeycomb");

    @Translate("Honey template printed to logs!")
    public static final MutableComponent HONEY_TEMPLATE_PRINTED = Component.translatable("command.resourcefulbees.template.honey");

    @Translate("Trait template printed to logs!")
    public static final MutableComponent TRAIT_TEMPLATE_PRINTED = Component.translatable("command.resourcefulbees.template.trait");

    @Translate("Block: %s")
    public static final String MUTATION_BLOCK = "gui.resourcefulbees.mutation.block";

    @Translate("Weight: %s")
    public static final String WEIGHT = "gui.resourcefulbees.mutation.weight";

    @Translate("Chance: %s")
    public static final String CHANCE = "gui.resourcefulbees.mutation.chance";


    //TODO Decide if the unused fields in this class will ever need to be used and if not remove them
    // UNUSED FIELDS BELOW THIS LINE!!!!

    public static class LightLevel {

        @Translate("Any")
        public static final MutableComponent ANY = Component.translatable("gui.resourcefulbees.light.any");

        @Translate("Day")
        public static final MutableComponent DAY = Component.translatable("gui.resourcefulbees.light.day");

        @Translate("Night")
        public static final MutableComponent NIGHT = Component.translatable("gui.resourcefulbees.light.night");
    }

    public static class Booleans {

        @Translate("Yes")
        public static final MutableComponent YES = Component.translatable("gui.resourcefulbees.yes");

        @Translate("No")
        public static final MutableComponent NO = Component.translatable("gui.resourcefulbees.no");

    }

    public static class Sizes {

        @Translate("Tiny")
        public static final MutableComponent TINY = Component.translatable("gui.resourcefulbees.size.tiny");

        @Translate("Small")
        public static final MutableComponent SMALL = Component.translatable("gui.resourcefulbees.size.small");

        @Translate("Regular")
        public static final MutableComponent REGULAR = Component.translatable("gui.resourcefulbees.size.regular");

        @Translate("Large")
        public static final MutableComponent LARGE = Component.translatable("gui.resourcefulbees.size.large");

        @Translate("Giant")
        public static final MutableComponent GIANT = Component.translatable("gui.resourcefulbees.size.giant");
    }

}
