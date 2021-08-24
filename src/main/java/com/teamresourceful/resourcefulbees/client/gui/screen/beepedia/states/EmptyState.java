package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaState;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import org.jetbrains.annotations.NotNull;

public class EmptyState extends BeepediaState {

    public EmptyState(@NotNull BeepediaPage page, @NotNull BeepediaScreenArea screenArea) {
        super(page, screenArea);
    }

    @Override
    protected void initState() {
        // is not used
    }

    @Override
    protected void postInitState() {
        // is not used
    }
}
