package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaState;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.SubPageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.*;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees.BeeHoneyCombPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees.BeeCombatPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees.BeeInfoPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees.BeeTraitListPage;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;

import java.util.LinkedHashMap;
import java.util.Map;

public class BeeState extends BeepediaState {

    /**
     * all of the available states
     */
    public static Map<String, ? extends BeepediaState> states = new LinkedHashMap<>();

    private static boolean init = false;

    CustomBeeData beeData;

    public BeeState(CustomBeeData beeData, BeepediaScreenArea screenArea) {
        super(new BeePage(beeData), screenArea);
        this.beeData = beeData;
    }

    public static Map<String, BeepediaPage> getSubstates(CustomBeeData beeData) {
        return null;
    }

    public static void initSubStates() {
        if (init) return;
        BeepediaScreenArea screenArea = new BeepediaScreenArea(1, 1, 1, 1);
        states.put(SubPageTypes.INFO.name(), new EmptyState(new BeeInfoPage(), screenArea));
        states.put(SubPageTypes.COMBAT.name(), new EmptyState(new BeeCombatPage(), screenArea));
        states.put(SubPageTypes.TRAITS.name(), new EmptyState(new BeeTraitListPage(), screenArea));
        states.put(SubPageTypes.HONEYCOMB.name(), new BeeHoneycombState(new BeeHoneyCombPage(screenArea), screenArea));
        init = true;
    }

    @Override
    protected void initState() {

    }

    @Override
    protected void postInitState() {

    }
}
