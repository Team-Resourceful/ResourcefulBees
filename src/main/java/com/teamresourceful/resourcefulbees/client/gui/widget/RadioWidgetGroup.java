package com.teamresourceful.resourcefulbees.client.gui.widget;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RadioWidgetGroup<T> {
    Map<T, TooltipWidget> map = new HashMap<>();

    public RadioWidgetGroup(T defaultKey, TooltipWidget defaultWidget) {
        map.put(defaultKey, defaultWidget);
        defaultWidget.active = false;
    }

    public void add(T key, TooltipWidget widget) {
        map.put(key, widget);
        widget.active = true;
    }

    /***
     * disabled = selected, buttons are weird
     *
     * @param key the value to set as disabled
     */
    public void updateGroup(T key){
        map.forEach(((t, widget) -> widget.active = t != key));
    }

}
