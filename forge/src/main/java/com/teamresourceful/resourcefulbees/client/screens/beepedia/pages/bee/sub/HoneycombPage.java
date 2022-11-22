package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.common.lib.builders.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.builders.BeehiveTier;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.common.utils.CycleableList;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HoneycombPage extends Screen implements TooltipProvider {

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

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return TooltipProvider.getTooltips(this.renderables, mouseX, mouseY);
    }
}
