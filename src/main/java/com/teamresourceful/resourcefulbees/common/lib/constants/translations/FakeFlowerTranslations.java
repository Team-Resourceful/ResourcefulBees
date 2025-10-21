package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FakeFlowerTranslations {

    private FakeFlowerTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @Translate("Fake Golden Flower")
    public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.fake_flower");

    @Translate("Visiting bees will leave pollen that can be harvested")
    public static final MutableComponent HARVEST = Component.translatable("block.resourcefulbees.fake_flower.tooltip.harvest");
}
