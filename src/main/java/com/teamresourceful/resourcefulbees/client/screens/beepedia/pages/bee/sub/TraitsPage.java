package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub;

import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class TraitsPage extends Screen implements TooltipProvider {
    private final List<BeeTrait> traits;

    public TraitsPage(TraitData traits) {
        super(CommonComponents.EMPTY);
        this.traits = traits.getTraits()
                .stream()
                .map(TraitRegistry.getRegistry()::getTrait)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    protected void init() {
        var list = addRenderableWidget(new SelectionList(1, 0, 182, 111, 26, ignored -> {}));
        list.updateEntries(this.traits.stream().map(TraitEntry::new).toList());
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return TooltipProvider.getTooltips(this.renderables, mouseX, mouseY);
    }
}

