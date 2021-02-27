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
    public final int height;
    public final int width;
    public final int itemHeight;
    public int scrollPos = 0;
    public final TabImageButton button;
    protected boolean active = false;
    Map<String, ? extends BeepediaPage> list;
    Map<String, BeepediaPage> reducedList = new TreeMap<>();

    public ButtonList(int xPos, int yPos, int height, int width, int itemHeight, TabImageButton button, Map<String, ? extends BeepediaPage> list) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
        this.width = width;
        this.itemHeight = itemHeight;
        this.list = list;
        this.button = button;
        if (this instanceof SubButtonList) return;
        updateReducedList(null);
        list.forEach((s, b) -> b.listButton.setParent(this));
    }

    public int getScrollPos() {
        return scrollPos;
    }

    public void updateReducedList(String search) {
        reducedList.clear();
        if (search != null && !search.isEmpty()) {
            list.forEach((s, b) -> {
                if (s.contains(search.toLowerCase()) || b.getSearch().toLowerCase().contains(search.toLowerCase())) {
                    reducedList.put(s, b);
                }
            });
        } else {
            reducedList = new HashMap<>(list);
        }
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
        list.forEach((s, b) -> {
            if (b.listButton != null) b.listButton.visible = active;
        });
    }

    public void setScrollPos(int scrollPos) {
        this.scrollPos = scrollPos;
    }
}
