package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.OutputLocationGroup;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.ProcessContainerData;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TerminalInputHomePanel extends AbstractInfoPanel<CentrifugeInputEntity> {

    private ProcessContainerData processData = new ProcessContainerData();
    private final CentrifugeEnergyStorage energyStorage;

    public TerminalInputHomePanel(int x, int y, boolean displayTitleBar, CentrifugeEnergyStorage energyStorage) {
        super(x, y, displayTitleBar);
        this.energyStorage = energyStorage;
        init();
    }

    public TerminalInputHomePanel(int x, int y, CentrifugeEnergyStorage energyStorag) {
        this(x, y, true, energyStorag);
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        this.selectedEntity = (CentrifugeInputEntity) selectedEntity;
    }

    public void setProcessData(@NotNull ProcessContainerData processData) {
        this.processData = processData;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        super.render(graphics, mouseX, mouseY, partialTicks);
        int tX = x + 14;
        int tY = displayTitleBar ? y + 30 : y + 14;

        drawLocationString(graphics, CentrifugeUtils.formatBlockPos(selectedEntity.getBlockPos()), tX, tY);
        drawRecipe(graphics, selectedEntity.getFilterRecipeID(), CentrifugeTranslations.FILTER_RECIPE, CentrifugeTranslations.FILTER_SLOT_NOT_SET, tX, tY + 8);
        drawRecipe(graphics, selectedEntity.getProcessRecipeID(), CentrifugeTranslations.PROCESS_RECIPE, CentrifugeTranslations.RECIPE_NOT_PROCESSED, tX, tY + 16);
        drawEnergyPerTickString(graphics, processData.getEnergy(), tX, tY + 24);
        drawProcessingStageString(graphics, selectedEntity.getProcessStage(), tX, tY + 32);
        int timeLeft = !selectedEntity.getProcessStage().isProcessing() || energyStorage.isEmpty() ? Integer.MAX_VALUE : processData.getTime();
        drawProcessTimeLeftString(graphics, timeLeft, tX, tY + 40);
        drawOutputTypeString(graphics, CentrifugeTranslations.ITEM.getString(), tX, tY + 48);
        drawOutputString(graphics, selectedEntity.getItemOutputs(), tX, tY + 56);
        drawOutputTypeString(graphics, CentrifugeTranslations.FLUID.getString(), tX, tY + 80);
        drawOutputString(graphics, selectedEntity.getFluidOutputs(), tX, tY + 88);
    }

    private static void drawProcessTimeLeftString(GuiGraphics graphics, int timeLeft, int x, int y) {
        drawString(graphics, Component.translatable(CentrifugeTranslations.TIME_LEFT, formatTicksAsSeconds(timeLeft)), x, y);
    }

    private static void drawEnergyPerTickString(GuiGraphics graphics, int energy, int x, int y) {
        drawString(graphics, Component.translatable(CentrifugeTranslations.ENERGY_USAGE, energy), x, y);
    }

    @NotNull
    private static String formatRecipeID(String recipe) {
        return recipe.replace(recipe.subSequence(0, recipe.lastIndexOf("/") + 1), "");
    }

    private static void drawLocationString(GuiGraphics graphics, String location, int x, int y) {
        drawString(graphics, Component.translatable(CentrifugeTranslations.LOCATION, location), x, y);
    }

    private static void drawRecipe(GuiGraphics graphics, ResourceLocation recipeID, String translation, MutableComponent error, int x, int y) {
        if (recipeID != null) {
            String recipe = formatRecipeID(recipeID.getPath());
            drawRecipeString(graphics, translation, recipe, x, y);
        } else {
            drawRecipeString(graphics, translation, error.getString(), x, y);
        }
    }

    private static void drawRecipeString(GuiGraphics graphics, String translation, String recipeID, int x, int y) {
        drawString(graphics, Component.translatable(translation, recipeID), x, y);
    }

    private static void drawProcessingStageString(GuiGraphics graphics, ProcessStage processingStage, int x, int y) {
        drawString(graphics, Component.translatable(CentrifugeTranslations.STAGE, processingStage), x, y);
    }

    private static void drawOutputTypeString(GuiGraphics graphics, String type, int x, int y) {
        drawString(graphics, Component.translatable(CentrifugeTranslations.OUTPUTS, type), x, y);
    }

    private static void drawOutputString(GuiGraphics graphics, OutputLocationGroup<?, ?, ?> outputLocationGroup, int x, int y) {
        for (int i = 0; i < 3; i++) {
            drawOutputString(graphics, i + 1, getOutputPos(outputLocationGroup, i), x, y + (i * 8));
        }
    }

    private static void drawOutputString(GuiGraphics graphics, int index, String location, int x, int y) {
        drawString(graphics, Component.translatable(CentrifugeTranslations.OUTPUT_NUM, index, location), x, y);
    }

    private static void drawString(GuiGraphics graphics, Component component, int x, int y) {
        graphics.drawString(TextUtils.TERMINAL_FONT_8, component, x, y, TextUtils.FONT_COLOR_1, false);
    }

    private static String getOutputPos(OutputLocationGroup<?, ?, ?> outputLocationGroup, int index) {
        BlockPos output = outputLocationGroup.get(index).getPos();
        return output == null ? CentrifugeTranslations.OUTPUT_NOT_LINKED.getString() : CentrifugeUtils.formatBlockPos(output);
    }

    private static String formatTicksAsSeconds(int ticks) {
        if (ticks == Integer.MAX_VALUE) return CentrifugeTranslations.UNKNOWN.getString();
        float seconds = ticks * 0.05f;
        return String.format("%06.2fs", seconds);
    }
}
