package com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.terminal.input;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.OutputLocationGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TerminalInputHomePanel extends AbstractInfoPanel<CentrifugeInputEntity> {

    public TerminalInputHomePanel(int x, int y) {
        super(x, y);
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        this.selectedEntity = (CentrifugeInputEntity) selectedEntity;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        ClientUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x+2, y+16, 21, 0, 233, 3);
        TextUtils.tf12DrawCenteredStringNoShadow(stack, selectedEntity.getDisplayName(), x+118.5f, y+6.5f, TextUtils.FONT_COLOR_1);
        int tX = x+14;
        int tY = y+14;

        drawLocationString(stack, CentrifugeUtils.formatBlockPos(selectedEntity.getBlockPos()), tX+6, tY+16);
        drawRecipeString(stack, selectedEntity.getFilterRecipeID(), tX+6, tY+24);
        drawProcessingStageString(stack, selectedEntity.getProcessStage(), tX+6, tY+32);
        drawOutputTypeString(stack, "Item", tX+6, tY+40);
        drawOutputString(stack, selectedEntity.getItemOutputs(), tX+6, tY+48);
        drawOutputTypeString(stack, "Fluid", tX+6, tY+72);
        drawOutputString(stack, selectedEntity.getFluidOutputs(), tX+6, tY+80);
    }

    //TODO make these translatable texts
    private static void drawLocationString(PoseStack stack, String location, int x, int y) {
        drawString(stack, "Location: " + location, x, y);
    }

    private static void drawRecipeString(PoseStack stack, ResourceLocation recipeID, int x, int y) {
        drawString(stack, "Recipe: " + recipeID, x, y);
    }

    private static void drawProcessingStageString(PoseStack stack, ProcessStage processingStage, int x, int y) {
        drawString(stack, "Processing Stage: " + processingStage, x, y);
    }

    private static void drawOutputTypeString(PoseStack stack, String type, int x, int y) {
        drawString(stack, type + " Outputs: ", x, y);
    }

    private static void drawOutputString(PoseStack stack, OutputLocationGroup<?> outputLocationGroup, int x, int y) {
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

    private static String getOutputPos(OutputLocationGroup<?> outputLocationGroup, int index) {
        BlockPos output = outputLocationGroup.get(index).getPos();
        //TODO make this translatable
        return output == null ? "Output not linked!" : CentrifugeUtils.formatBlockPos(output);
    }
}
