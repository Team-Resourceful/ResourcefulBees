package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.trait;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.client.component.ScaledItemStackDisplayWidget;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import earth.terrarium.olympus.client.ui.context.DividerWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class TraitHeader {

    private TraitHeader() {
    }

    public static void build(LinearViewLayout layout, Trait trait) {
        LinearLayout header = LinearLayout.horizontal().spacing(6);

        header.addChild(new ScaledItemStackDisplayWidget(new ItemStack(trait.displayItem())));

        LinearLayout text = LinearLayout.vertical().spacing(1);

        text.addChild(Widgets.text(trait.getDisplayName(), TextWidget::withLeftAlignment));
        text.addChild(Widgets.text(Component.literal(trait.name()), widget -> widget.withLeftAlignment().withColor(Color.parse("gray"))));
        header.addChild(text, LayoutSettings::alignVerticallyBottom);
        layout.withChild(header);
        layout.withChild(new DividerWidget());
    }
}