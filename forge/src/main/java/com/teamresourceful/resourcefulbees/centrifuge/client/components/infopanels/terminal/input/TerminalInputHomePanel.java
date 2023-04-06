package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input;

import com.mojang.blaze3d.vertex.PoseStack;
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
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        super.render(stack, mouseX, mouseY, partialTicks);
        int tX = x+14;
        int tY = displayTitleBar ? y+30 : y+14;

        drawLocationString(stack, CentrifugeUtils.formatBlockPos(selectedEntity.getBlockPos()), tX, tY);
        drawRecipe(stack, selectedEntity.getFilterRecipeID(), CentrifugeTranslations.FILTER_RECIPE, CentrifugeTranslations.FILTER_SLOT_NOT_SET, tX, tY+8);
        drawRecipe(stack, selectedEntity.getProcessRecipeID(), CentrifugeTranslations.PROCESS_RECIPE, CentrifugeTranslations.RECIPE_NOT_PROCESSED, tX, tY+16);
        drawEnergyPerTickString(stack, processData.getEnergy(), tX, tY+24);
        drawProcessingStageString(stack, selectedEntity.getProcessStage(), tX, tY+32);
        int timeLeft = !selectedEntity.getProcessStage().isProcessing() || energyStorage.isEmpty() ? Integer.MAX_VALUE : processData.getTime();
        drawProcessTimeLeftString(stack, timeLeft, tX, tY+40);
        drawOutputTypeString(stack, CentrifugeTranslations.ITEM.getString(), tX, tY+48);
        drawOutputString(stack, selectedEntity.getItemOutputs(), tX, tY+56);
        drawOutputTypeString(stack, CentrifugeTranslations.FLUID.getString(), tX, tY+80);
        drawOutputString(stack, selectedEntity.getFluidOutputs(), tX, tY+88);
    }

    private static void drawProcessTimeLeftString(PoseStack stack, int timeLeft, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.TIME_LEFT, formatTicksAsSeconds(timeLeft)), x, y);
    }

    private static void drawEnergyPerTickString(PoseStack stack, int energy, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.ENERGY_USAGE, energy), x, y);
    }

    @NotNull
    private static String formatRecipeID(String recipe) {
        return recipe.replace(recipe.subSequence(0, recipe.lastIndexOf("/")+1), "");
    }

    private static void drawLocationString(PoseStack stack, String location, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.LOCATION, location), x, y);
    }

    private static void drawRecipe(PoseStack stack, ResourceLocation recipeID, String translation, MutableComponent error, int x, int y) {
        if (recipeID != null) {
            String recipe = formatRecipeID(recipeID.getPath());
            drawRecipeString(stack, translation, recipe, x, y);
        } else {
            drawRecipeString(stack, translation, error.getString(), x, y);
        }
    }

    private static void drawRecipeString(PoseStack stack, String translation, String recipeID, int x, int y) {
        drawString(stack, Component.translatable(translation, recipeID), x, y);
    }

    private static void drawProcessingStageString(PoseStack stack, ProcessStage processingStage, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.STAGE, processingStage), x, y);
    }

    private static void drawOutputTypeString(PoseStack stack, String type, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.OUTPUTS, type), x, y);
    }

    private static void drawOutputString(PoseStack stack, OutputLocationGroup<?,?,?> outputLocationGroup, int x, int y) {
        for (int i = 0; i < 3; i++) {
            drawOutputString(stack, i+1, getOutputPos(outputLocationGroup, i), x, y+(i*8));
        }
    }

    private static void drawOutputString(PoseStack stack, int index, String location, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.OUTPUT_NUM, index, location), x, y);
    }

    private static void drawString(PoseStack stack, Component component, int x, int y) {
        TextUtils.TERMINAL_FONT_8.draw(stack, component, x, y, TextUtils.FONT_COLOR_1);
    }

    private static String getOutputPos(OutputLocationGroup<?,?,?> outputLocationGroup, int index) {
        BlockPos output = outputLocationGroup.get(index).getPos();
        return output == null ? CentrifugeTranslations.OUTPUT_NOT_LINKED.getString() : CentrifugeUtils.formatBlockPos(output);
    }

    private static String formatTicksAsSeconds(int ticks) {
        if(ticks == Integer.MAX_VALUE) return CentrifugeTranslations.UNKNOWN.getString();
        float seconds = ticks*0.05f;
        return String.format("%06.2fs", seconds);
    }
}
