package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.client.screen.base.RenderingScreen;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import net.minecraft.network.chat.CommonComponents;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TraitsPage extends RenderingScreen {

    private final Consumer<Trait> traitConsumer;

    private final List<Trait> traits;

    public TraitsPage(BeeTraitData traits, Consumer<Trait> traitConsumer) {
        super(CommonComponents.EMPTY);
        this.traitConsumer = traitConsumer;
        this.traits = traits.traits()
                .stream()
                .map(TraitRegistry.get()::getTrait)
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
}

