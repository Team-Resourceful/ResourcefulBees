package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaState;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.StateEnum;

import java.util.Map;

public class CombState extends BeepediaState {

    public CombState(BeepediaState parent, BeepediaPage page, Map<StateEnum, BeepediaPage> subStates, StateEnum defaultState) {
        super(parent, page, subStates, defaultState);
    }

    @Override
    protected void initState() {

    }
}
