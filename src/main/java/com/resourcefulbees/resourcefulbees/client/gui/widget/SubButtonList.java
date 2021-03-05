package com.resourcefulbees.resourcefulbees.client.gui.widget;


import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SubButtonList extends ButtonList {
    SortedMap<String, ListButton> subList;

    public SubButtonList(int xPos, int yPos, int width, int height, int itemHeight, TabImageButton button, SortedMap<String, ListButton> list) {
        super(xPos, yPos, width, height, itemHeight, button, null);
        this.subList = list;
        list.forEach((s, b) -> b.setParent(this));
    }

    @Override
    public void updateReducedList(String search) {
        if (reducedList == null) return;
        super.updateReducedList(search);
    }

    @Override
    public void updatePos(int newPos) {
        // update position of list
        if (height > subList.size() * itemHeight) return;
        scrollPos += newPos;
        if (scrollPos > 0) scrollPos = 0;
        else if (scrollPos < -(subList.size() * itemHeight - height))
            scrollPos = -(subList.size() * itemHeight - height);
    }

    @Override
    public void updateList() {
        // update each button
        AtomicInteger counter = new AtomicInteger();
        subList.forEach((s, b) -> {
            if (b == null) return;
            b.x = xPos;
            b.y = yPos + scrollPos + counter.get() * itemHeight;
            counter.getAndIncrement();
        });
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (button != null) button.active = !active;
        subList.forEach((s, b) -> {
            if (b != null) b.visible = active;
        });
    }
}