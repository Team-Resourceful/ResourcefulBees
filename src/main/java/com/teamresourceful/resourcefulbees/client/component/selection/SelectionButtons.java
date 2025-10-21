package com.teamresourceful.resourcefulbees.client.component.selection;

import com.teamresourceful.resourcefulbees.client.component.SlotButton;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.state.BeepediaState;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;

import java.util.List;

public class SelectionButtons extends ParentWidget {

    private final BeepediaScreen screen;

    public SelectionButtons(int x, int y, BeepediaScreen screen) {
        super(x, y);
        this.screen = screen;
        init();
    }

    @Override
    protected void init() {
        addRenderableWidget(createButton(x, y, BeepediaState.Type.BEES))
                .setTooltipProvider(() -> List.of(BeepediaTranslations.BEES));
        addRenderableWidget(createButton(x + 22, y, BeepediaState.Type.TRAITS))
                .setTooltipProvider(() -> List.of(BeepediaTranslations.TRAITS));
        addRenderableWidget(createButton(x + 44, y, BeepediaState.Type.HONEY))
                .setTooltipProvider(() -> List.of(BeepediaTranslations.HONEY));
    }

    private SlotButton createButton(int x, int y, BeepediaState.Type type) {
        return new SlotButton(x + 28, y, type.texture, () -> this.screen.getState().getType().equals(type), () -> {
            this.screen.getState().setType(type);
            screen.updateSelections();
        });
    }
}
