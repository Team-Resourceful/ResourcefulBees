package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ButtonList {
    public final int xPos;
    public final int yPos;
    public int defaultHeight;
    public int height;
    public final int width;
    public final int itemHeight;
    public int scrollPos = 0;
    public final TabImageButton button;
    protected boolean active = false;
    private String lastSearch;
    Map<String, ? extends BeepediaPage> list;
    Map<String, BeepediaPage> reducedList = new TreeMap<>();

    public ButtonList(int xPos, int yPos, int height, int width, int itemHeight, TabImageButton button, Map<String, ? extends BeepediaPage> list) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.defaultHeight = height;
        this.height = defaultHeight;
        this.width = width;
        this.itemHeight = itemHeight;
        this.list = list;
        this.button = button;
        if (this instanceof SubButtonList) return;
        updateReducedList(null);
        list.forEach((s, b) -> b.listButton.setParent(this));
    }

    public void setSearchHeight() {
        this.height = defaultHeight - 12;
    }

    public void resetHeight() {
        this.height = defaultHeight;
    }

    public int getScrollPos() {
        return scrollPos;
    }

    public void updateReducedList(String search) {
        scrollPos = 0;
        reducedList.clear();
        if (search != null && !search.isEmpty()) {
            list.forEach((s, b) -> {
                if (s.contains(search.toLowerCase()) || b.getSearch().toLowerCase().contains(search.toLowerCase())) {
                    reducedList.put(s, b);
                    if (active) b.listButton.visible = true;
                } else {
                    if (active) b.listButton.visible = false;
                }
            });
        } else {
            if (active) list.forEach((s, b) -> b.listButton.visible = true);
            reducedList = new HashMap<>(list);
        }
        lastSearch = search;
    }

    public void updatePos(int newPos) {
        // update position of list
        if (height > reducedList.size() * itemHeight) return;
        scrollPos += newPos;
        if (scrollPos > 0) scrollPos = 0;
        else if (scrollPos < -(reducedList.size() * itemHeight - height))
            scrollPos = -(reducedList.size() * itemHeight - height);
    }

    public void updateList() {
        // update each button
        AtomicInteger counter = new AtomicInteger();
        reducedList.forEach((s, b) -> {
            b.updateListPosition(xPos, (yPos + scrollPos + counter.get() * itemHeight));
            counter.getAndIncrement();
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        if (button != null) button.active = !active;
        if (!BeepediaScreen.listChanged()) return;
        list.forEach((s, b) -> {
            if (b.listButton != null) b.listButton.visible = active;
        });
        boolean searchVisible = BeepediaScreen.isSearchVisible();
        boolean doUpdateList = active && searchVisible;
        if (doUpdateList) updateReducedList(BeepediaScreen.getSearch());
    }

    public void setScrollPos(int scrollPos) {
        this.scrollPos = scrollPos;
    }
}
