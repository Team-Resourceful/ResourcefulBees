package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitRegistry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TraitsPage extends Screen implements TooltipProvider {

    private final Consumer<Trait> traitConsumer;

    private final List<Trait> traits;

    public TraitsPage(BeeTraitData traits, Consumer<Trait> traitConsumer) {
        super(CommonComponents.EMPTY);
        this.traitConsumer = traitConsumer;
        this.traits = traits.traits()
                .stream()
                .map(TraitRegistry.getRegistry()::getTrait)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    protected void init() {
        var list = addRenderableWidget(new SelectionList<TraitEntry>(1, 0, 182, 111, 26, entry -> {
            if (entry != null) {
                traitConsumer.accept(entry.getTrait());
            }
        }));
        list.updateEntries(this.traits.stream().map(TraitEntry::new).toList());
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return TooltipProvider.getTooltips(this.renderables, mouseX, mouseY);
    }
}

