package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class JeiTranslations {

    private JeiTranslations() {
        throw new UtilityClassError();
    }

    @Translate("Bee Breeding")
    public static final MutableComponent BREEDING = Component.translatable("gui.resourcefulbees.jei.category.breeding");

    @Translate("Centrifuge")
    public static final MutableComponent CENTRIFUGE = Component.translatable("gui.resourcefulbees.jei.category.centrifuge");

    @Translate("Bee Flowers")
    public static final MutableComponent FLOWERS = Component.translatable("gui.resourcefulbees.jei.category.bee_pollination_flowers");

    @Translate("Beehive Outputs")
    public static final MutableComponent HIVE = Component.translatable("gui.resourcefulbees.jei.category.hive");

    @Translate("Mutations")
    public static final MutableComponent MUTATIONS = Component.translatable("gui.resourcefulbees.jei.category.mutations");

    @Translate("Solidification Chamber")
    public static final MutableComponent SOLIDIFICATION = Component.translatable("gui.resourcefulbees.jei.category.solidification");

    @Translate("Chance that this will succeed in creating a new bee.")
    public static final MutableComponent BREED_CHANCE_INFO = Component.translatable("gui.resourcefulbees.jei.category.breed_chance.info");

    @Translate("[Press Shift to show NBT]")
    public static final MutableComponent NBT = Component.translatable("gui.resourcefulbees.jei.tooltip.show_nbt");

    @Translate("Bee block mutation requires there to be a valid hive for the bee to go into & the bee to have nectar.")
    public static final MutableComponent MUTATION_INFO = Component.translatable("gui.resourcefulbees.jei.category.mutation.info");

    @Translate("Chance that this will be chosen succeed in mutating.")
    public static final MutableComponent MUTATION_WEIGHT_CHANCE_INFO = Component.translatable("gui.resourcefulbees.jei.category.mutation_weight_chance.info");

    @Translate("Weight: %d")
    public static final String CENTRIFUGE_WEIGHT = "gui.resourcefulbees.jei.category.centrifuge.weight";

    @Translate("Weight: SLOT EMPTY")
    public static final MutableComponent CENTRIFUGE_WEIGHT_EMPTY = Component.translatable("gui.resourcefulbees.jei.category.centrifuge.weight.empty");

    @Translate("Pool Chance: %d")
    public static final String CENTRIFUGE_CHANCE = "gui.resourcefulbees.jei.category.centrifuge.chance";

    @Translate("Pool Chance: SLOT EMPTY")
    public static final MutableComponent CENTRIFUGE_CHANCE_EMPTY = Component.translatable("gui.resourcefulbees.jei.category.centrifuge.chance.empty");

    @Translate("Click or Right-Click for more info!")
    public static final MutableComponent CLICK_INFO = Component.translatable("tooltip.resourcefulbees.jei.click_bee_info");

    @Translate("Flow Hive Outputs")
    public static final MutableComponent FLOW_HIVE_OUTPUTS = Component.translatable("gui.resourcefulbees.jei.category.flowhive");

    @Translate("Drain Rate: %s mB/t")
    public static final String DRAIN_RATE = "gui.resourcefulbees.jei.category.generator.drain_rate";

    @Translate("Energy: %s RF")
    public static final String ENERGY = "gui.resourcefulbees.jei.category.generator.energy";

    @Translate("Fill Rate: %s RF/t")
    public static final String FILL_RATE = "gui.resourcefulbees.jei.category.generator.fill_rate";
}
