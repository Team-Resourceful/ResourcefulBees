package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub;

import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class HoneycombPage extends Screen {

    private final OutputVariation honeycomb;

    public HoneycombPage(OutputVariation honeycomb) {
        super(CommonComponents.EMPTY);
        this.honeycomb = honeycomb;
    }

    @Override
    protected void init() {

    }
}
