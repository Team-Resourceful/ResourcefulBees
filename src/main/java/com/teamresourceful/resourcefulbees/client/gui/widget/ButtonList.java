package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Deprecated
@OnlyIn(Dist.CLIENT)
public class ButtonList {
    public final int xPos;
    public final int yPos;
    private final int defaultHeight;
    protected int height;
    public final int width;
    public final int itemHeight;
    protected int scrollPos = 0;
    public final TabImageButton button;
    protected boolean active = false;
    private final Map<String, ? extends BeepediaPage> list;
    SortedMap<String, BeepediaPage> reducedList = new TreeMap<>();

    public ButtonList(int xPos, int yPos, int width, int height, int itemHeight, TabImageButton button, Map<String, ? extends BeepediaPage> list) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.defaultHeight = height;
        this.height = defaultHeight;
        this.width = width;
        this.itemHeight = itemHeight;
        this.list = list;
        this.button = button;
        if (this instanceof SubButtonList) return;
        updateReducedList(null, true);
//        list.forEach((s, b) -> b.listButton.setParent(this));
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

    public void updateReducedList(String search, boolean resetHeight) {
//        if (resetHeight) scrollPos = 0;
//        reducedList.clear();
//        if (search != null && !search.isEmpty()) {
//            list.forEach((s, b) -> reduceList(search, s, b));
//        } else {
//            if (active) list.forEach((s, b) -> b.listButton.visible = true);
//            reducedList = new TreeMap<>(list);
//        }
    }

    private void reduceList(String search, String s, BeepediaPage b) {
//        boolean pageFound = (b instanceof BeePage && ((BeePage) b).getBeeFromSearch(search)) ||
//                (b instanceof HoneyPage && ((HoneyPage) b).getHoneyFromSearch(search)) ||
//                (b instanceof TraitPage && ((TraitPage) b).getTraitFromSearch(search));
//        if (pageFound) {
//            reducedList.put(s, b);
//            if (active) b.listButton.visible = true;
//        } else {
//            if (active) b.listButton.visible = false;
//        }
    }

    public Map<String, ? extends BeepediaPage> getList() {
        return list;
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
//        // update each button
//        AtomicInteger counter = new AtomicInteger();
//        reducedList.forEach((s, b) -> {
//            b.updateListPosition(xPos, (yPos + scrollPos + counter.get() * itemHeight));
//            counter.getAndIncrement();
//        });
    }

    public void setActive(boolean active) {
        setActive(active, false);
    }

    public void setActive(boolean active, boolean forceRedraw) {
//        this.active = active;
//        if (button != null) button.active = !active;
//        if (!BeepediaScreen.listChanged() && !forceRedraw) return;
//        list.forEach((s, b) -> {
//            if (b.listButton != null) b.listButton.visible = active;
//        });
//        boolean searchVisible = BeepediaScreen.isSearchVisible();
//        boolean doUpdateList = active && searchVisible;
//        if (doUpdateList) updateReducedList(BeepediaScreen.getSearch(), BeepediaScreen.searchUpdated());
    }

    public void setScrollPos(int scrollPos) {
        if (height > reducedList.size() * itemHeight) return;
        this.scrollPos = scrollPos;
        if (this.scrollPos > 0) this.scrollPos = 0;
        else if (this.scrollPos < -(reducedList.size() * itemHeight - height))
            this.scrollPos = -(reducedList.size() * itemHeight - height);
    }
}
