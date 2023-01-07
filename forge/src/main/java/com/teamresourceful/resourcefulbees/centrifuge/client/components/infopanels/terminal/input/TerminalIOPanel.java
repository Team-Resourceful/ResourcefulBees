package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.OutputLocationSelectionWidget;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TerminalIOPanel extends AbstractInfoPanel<CentrifugeInputEntity> {

    //todo make translatable
    private static final Component INSTRUCTIONS = Component.literal("Please select the output location for each recipe output slot.");
    private final CentrifugeOutputType outputType;
    private final List<BlockPos> outputsList;

    public TerminalIOPanel(int x, int y, CentrifugeOutputType outputType, List<BlockPos> outputsList, boolean displayTitleBar) {
        super(x, y, displayTitleBar);
        this.outputType = outputType;
        this.outputsList = outputsList;
        init();
    }

    public TerminalIOPanel(int x, int y, CentrifugeOutputType outputType, List<BlockPos> outputsList) {
        this(x, y, outputType, outputsList, true);
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < 3; i++) {
            addRenderableWidget(new OutputLocationSelectionWidget(x+3+i*77, y+75, selectedEntity, i, outputType, outputsList));
        }
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        this.selectedEntity = (CentrifugeInputEntity) selectedEntity;
        clearOutputLocationSelectionWidgets();
        init();
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        super.render(stack, mouseX, mouseY, partialTicks);
        TextUtils.tf8DrawCenteredStringNoShadow(stack, INSTRUCTIONS, x+118.5f, y+36f, TextUtils.FONT_COLOR_1);
    }

    private void clearOutputLocationSelectionWidgets() {
        this.renderables.clear();
        this.children.clear();
    }
}
