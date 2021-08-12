package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaState;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.StateEnum;

import java.util.Map;

public class BeeState extends BeepediaState {

    public BeeState(BeepediaState parent, BeepediaPage defaultPage, Map<StateEnum, BeepediaPage> subStates, StateEnum defaultState) {
        super(parent, defaultPage, subStates, defaultState);
    }

    @Override
    protected void initState() {

    }
}
