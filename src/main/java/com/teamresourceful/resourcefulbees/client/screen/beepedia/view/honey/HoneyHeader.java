package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.honey;

import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
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

public final class HoneyHeader {

    private HoneyHeader() {
    }

    public static void build(LinearViewLayout layout, CustomHoneyData honey) {
        ItemStack bottle = new ItemStack(honey.getBottleData().bottle().get());

        LinearLayout header = LinearLayout.horizontal().spacing(6);
        header.addChild(new ScaledItemStackDisplayWidget(bottle));

        LinearLayout text = LinearLayout.vertical().spacing(1);

        text.addChild(Widgets.text(honey.displayName(), TextWidget::withLeftAlignment).withColor(Color.DEFAULT));
        text.addChild(Widgets.text(Component.literal(honey.name()), widget -> widget.withLeftAlignment().withColor(Color.parse("gray"))));

        header.addChild(text, LayoutSettings::alignVerticallyBottom);
        layout.withChild(header);
        layout.withChild(new DividerWidget());
    }
}
