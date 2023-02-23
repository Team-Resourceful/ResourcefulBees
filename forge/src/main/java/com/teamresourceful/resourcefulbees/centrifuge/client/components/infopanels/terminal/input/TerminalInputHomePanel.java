package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.input;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.OutputLocationGroup;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.ProcessContainerData;
import net.minecraft.core.BlockPos;
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

        ResourceLocation recipeID = selectedEntity.getFilterRecipeID();
        if (recipeID != null) {
            String recipe = formatRecipeID(recipeID.getPath());
            drawRecipeString(stack, recipe, tX, tY+8);
        } else {
            drawRecipeString(stack, "Filter slot not set!", tX, tY+8);
        }
        drawEnergyPerTickString(stack, processData.getEnergy(), tX, tY+16);
        drawProcessingStageString(stack, selectedEntity.getProcessStage(), tX, tY+24);
        int timeLeft = !selectedEntity.getProcessStage().isProcessing() || energyStorage.isEmpty() ? Integer.MAX_VALUE : processData.getTime();
        drawProcessTimeLeftString(stack, timeLeft, tX, tY+32);
        drawOutputTypeString(stack, "Item", tX, tY+40);
        drawOutputString(stack, selectedEntity.getItemOutputs(), tX, tY+48);
        drawOutputTypeString(stack, "Fluid", tX, tY+72);
        drawOutputString(stack, selectedEntity.getFluidOutputs(), tX, tY+80);
    }

    //TODO make these translatable texts
    private static void drawProcessTimeLeftString(PoseStack stack, int timeLeft, int x, int y) {
        drawString(stack, "Process Time Left: " + formatTicksAsSeconds(timeLeft), x, y);
    }

    private static void drawEnergyPerTickString(PoseStack stack, int energy, int x, int y) {
        drawString(stack, "Energy Usage: " + energy + " rf/t", x, y);
    }

    @NotNull
    private static String formatRecipeID(String recipe) {
        return recipe.replace(recipe.subSequence(0, recipe.lastIndexOf("/")+1), "");
    }

    private static void drawLocationString(PoseStack stack, String location, int x, int y) {
        drawString(stack, "Location: " + location, x, y);
    }

    private static void drawRecipeString(PoseStack stack, String recipeID, int x, int y) {
        drawString(stack, "Recipe: " + recipeID, x, y);
    }

    private static void drawProcessingStageString(PoseStack stack, ProcessStage processingStage, int x, int y) {
        drawString(stack, "Processing Stage: " + processingStage, x, y);
    }

    private static void drawOutputTypeString(PoseStack stack, String type, int x, int y) {
        drawString(stack, type + " Outputs: ", x, y);
    }

    private static void drawOutputString(PoseStack stack, OutputLocationGroup<?,?,?> outputLocationGroup, int x, int y) {
        for (int i = 0; i < 3; i++) {
            drawOutputString(stack, i+1, getOutputPos(outputLocationGroup, i), x, y+(i*8));
        }
    }

    private static void drawOutputString(PoseStack stack, int index, String location, int x, int y) {
        drawString(stack, "  Output #:" + index + " " + location, x, y);
    }

    private static void drawString(PoseStack stack, String string, int x, int y) {
        TextUtils.TERMINAL_FONT_8.draw(stack, string, x, y, TextUtils.FONT_COLOR_1);
    }

    private static String getOutputPos(OutputLocationGroup<?,?,?> outputLocationGroup, int index) {
        BlockPos output = outputLocationGroup.get(index).getPos();
        //TODO make this translatable
        return output == null ? "Output not linked!" : CentrifugeUtils.formatBlockPos(output);
    }

    private static String formatTicksAsSeconds(int ticks) {
        if(ticks == Integer.MAX_VALUE) return "Unknown";
        float seconds = ticks*0.05f;
        return String.format("%06.2fs", seconds);
    }
}
