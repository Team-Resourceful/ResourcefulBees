package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaState;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CollectedBeeInfoState extends BeepediaState {

    public CollectedBeeInfoState(@Nullable BeepediaState parent, @NotNull BeepediaPage page, @Nullable Map<String, BeepediaPage> subStates, @Nullable String defaultState, @NotNull BeepediaScreenArea screenArea) {
        super(parent, page, screenArea);
    }

    @Override
    protected void initState() {

    }

    @Override
    protected void postInitState() {

    }
}
