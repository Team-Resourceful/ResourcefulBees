package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FlowHiveTranslations {

    private FlowHiveTranslations() {
        throw new UtilityClassError();
    }

    @Translate("Collects liquid honey from bees")
    public static final MutableComponent INFO = Component.translatable("block.resourcefulbees.flowhive.tooltip.info");

    @Translate("Harvest using bottles or pipes")
    public static final MutableComponent HARVEST = Component.translatable("block.resourcefulbees.flowhive.tooltip.harvest");

    @Translate("Capacity: 16000mb")
    public static final MutableComponent CAPACITY = Component.translatable("block.resourcefulbees.flowhive.tooltip.capacity");
}
