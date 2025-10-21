package com.teamresourceful.resourcefulbees.client.component.selection;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;

public abstract class BaseListEntry extends ListEntry {

    private boolean focused = false;

    @Override
    public void setFocused(boolean bl) {
        this.focused = bl;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }
}
