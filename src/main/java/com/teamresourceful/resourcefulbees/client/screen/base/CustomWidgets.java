package com.teamresourceful.resourcefulbees.client.screen.base;

import com.teamresourceful.resourcefulbees.common.components.BatteryData;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.BaseWidget;

import java.util.function.Supplier;

public class CustomWidgets {

    public static BaseWidget battery(Supplier<BatteryData> data) {
        return Widgets.renderable(CustomWidgetRenderers.battery(data));
    }

    public static BaseWidget tank(Supplier<TankData> data) {
        return Widgets.renderable(CustomWidgetRenderers.tank(data));
    }
}
