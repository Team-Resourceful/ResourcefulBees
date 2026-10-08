package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaNavigator;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee.BeeTraitRow;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public final class BeeTraits {

    private static final int MIN_CARD_WIDTH = 70;
    private static final int COLUMN_GAP = 6;
    private static final int ROW_GAP = 4;

    private BeeTraits() {
    }

    public static void build(LinearViewLayout layout, CustomBeeData bee, int contentWidth, BeepediaNavigator navigator) {
        var traitData = bee.getTraitData();

        layout.withGap(5);

        layout.withChild(BeepediaScreenUtil.sectionTitle("Traits"));

        var traits = traitData.traits()
                .stream()
                .map(TraitRegistry.get()::getTrait)
                .filter(Objects::nonNull)
                .toList();

        if (traits.isEmpty()) {
            layout.withChild(Widgets.text(Component.literal("This bee has no traits.")));
            return;
        }

        int columns = Math.clamp((contentWidth + COLUMN_GAP) / (MIN_CARD_WIDTH + COLUMN_GAP), 1, 3);

        GridLayout grid = new GridLayout().columnSpacing(COLUMN_GAP).rowSpacing(ROW_GAP);
        GridLayout.RowHelper rows = grid.createRowHelper(columns);

        for (Trait trait : traits) {
            rows.addChild(new BeeTraitRow(trait, MIN_CARD_WIDTH, () -> navigator.openTrait(trait.name())));
        }

        layout.withChild(grid);
    }
}