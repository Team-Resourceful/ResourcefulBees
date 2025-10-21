package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.client.screen.base.RenderingScreen;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.common.collections.CycleableList;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

public class HoneycombPage extends RenderingScreen {

    private SelectionList<ListEntry> list;
    private int ticks = 0;

    private final OutputVariation honeycomb;

    public HoneycombPage(OutputVariation honeycomb) {
        super(CommonComponents.EMPTY);
        this.honeycomb = honeycomb;
    }

    @Override
    protected void init() {
        this.list = addRenderableWidget(new SelectionList<>(1, 0, 182, 111, 26, ignored -> {}));
        for (BeehiveTier value : BeehiveTier.values()) {
            CycleableList<ItemStack> hives = value.getDisplayItems()
                    .stream().map(ItemStack::new)
                    .collect(CycleableList::new, CycleableList::add, CycleableList::addAll);
            this.list.addEntry(new HoneycombEntry(hives, this.honeycomb.getHiveOutput(value), false));
        }
        for (ApiaryTier value : ApiaryTier.values()) {
            this.list.addEntry(new HoneycombEntry(Util.make(new CycleableList<>(), a -> a.add(new ItemStack(value.getItem()))), this.honeycomb.getApiaryOutput(value), true));
        }
    }

    @Override
    public void tick() {
        if (++this.ticks % 20 == 0) {
            this.ticks = 0;
            this.list.children().forEach(child -> ((HoneycombEntry) child).cycle());
        }
    }
}
