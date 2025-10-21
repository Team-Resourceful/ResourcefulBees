package com.teamresourceful.resourcefulbees.client.component.search;

import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.state.BeepediaState;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;

public class SearchBar extends ParentWidget {

    private final BeepediaScreen screen;

    public SearchBar(int x, int y, BeepediaScreen screen) {
        super(x, y);
        this.screen = screen;
        init();
    }

    @Override
    protected void init() {
        addRenderableWidget(new SearchButton(x, y, screen.getState(), BeepediaState.Sorting.FOUND, screen::updateSelections));
        addRenderableWidget(new SearchButton(x + 26, y, screen.getState(), BeepediaState.Sorting.ALPHABETICAL, screen::updateSelections));
        addRenderableWidget(new SearchButton(x + 52, y, screen.getState(), BeepediaState.Sorting.TRAITS, screen::updateSelections));
        addRenderableWidget(new SearchButton(x + 78, y, screen.getState(), BeepediaState.Sorting.MUTATION, screen::updateSelections));
    }
}
