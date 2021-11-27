package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.OutputLocations;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CentrifugeTerminalScreen extends BaseCentrifugeScreen<CentrifugeTerminalContainer> {

    private static final int COLOR = 0xffc9c9c9;
    private static final int TEXT_X = 110;

    private static final Rectangle HOME = new Rectangle(24, 58, 69, 13);
    private static final Rectangle INPUTS = new Rectangle(24, 72, 69, 13);
    private static final Rectangle ITEM_OUTS = new Rectangle(24, 86, 69, 13);
    private static final Rectangle FLUID_OUTS = new Rectangle(24, 100, 69, 13);
    private static final Rectangle DUMPS = new Rectangle(24, 114, 69, 13);
    private static final Map<Rectangle, Consumer<MatrixStack>> TABS = new HashMap<>();

    //protected static final Rectangle NAV_RIGHT = new Rectangle(86, 42, 9, 13);
    //protected static final Rectangle NAV_LEFT = new Rectangle(24, 42, 9, 13);

    private Rectangle selectedTab = HOME;
    private int selectedBlock = 0;
    private @Nullable AbstractGUICentrifugeEntity selectedEntity;

    public CentrifugeTerminalScreen(CentrifugeTerminalContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        TABS.put(HOME, this::drawTerminalData);
        TABS.put(INPUTS, this::drawInputData);
        TABS.put(ITEM_OUTS, this::drawItemOutputData);
        TABS.put(FLUID_OUTS, this::drawFluidOutputData);
        TABS.put(DUMPS, this::drawVoidData);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float pPartialTicks, int pX, int pY) {
        super.renderBg(matrix, pPartialTicks, pX, pY);
        //Render Info Pane Title Bar
        blit(matrix, leftPos + 104, topPos + 55, 21, 0, 233, 3);

        //Render Terminal Tabs
        blit(matrix, leftPos + 21, topPos + 39, 75, 165, 75, 91);
        blit(matrix, (int) (leftPos + selectedTab.getMaxX() - 7), topPos + selectedTab.y, 66, 196, 6, 13);

        //Render Nav Bar
        if (!selectedTab.equals(HOME)) {
            blit(matrix, leftPos + 21, topPos + 140, 0, 193, 75, 63);
        }
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrix, int pX, int pY) {
        super.renderLabels(matrix, pX, pY);
        //Draw Terminal Tab Labels
        drawTabLabels(matrix);

        TABS.get(selectedTab).accept(matrix);

        /*if (selectedEntity != null) {
            IReorderingProcessor text = selectedEntity.getDisplayName().getVisualOrderText();
            TERMINAL_FONT_8.draw(matrix, text, 58 - TERMINAL_FONT_8.width(text) / 2f, 45, 0xffffff);
        }*/
    }

    private void drawTabLabels(@NotNull MatrixStack matrix) {
        int labelLeft = 28;
        TERMINAL_FONT_8.draw(matrix, "Terminal", 58 - TERMINAL_FONT_8.width("Terminal") / 2f, 48, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Home", labelLeft, 64, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Inputs", labelLeft, 78, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Item Outputs", labelLeft, 92, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Fluid Outputs", labelLeft, 106, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Dumps", labelLeft, 120, COLOR);
    }

    private void drawASCII(@NotNull MatrixStack matrix) {
        /*
     ^^      .-=-=-=-.  ^^
 ^^        (`-=-=-=-=-`)         ^^
         (`-=-=-=-=-=-=-`)  ^^         ^^
   ^^   (`-=-=-=-=-=-=-=-`)   ^^                            ^^
       ( `-=-=-=-(@)-=-=-` )      ^^
       (`-=-=-=-=-=-=-=-=-`)  ^^
       (`-=-=-=-=-=-=-=-=-`)              ^^
       (`-=-=-=-=-=-=-=-=-`)                      ^^
       (`-=-=-=-=-=-=-=-=-`)  ^^
        (`-=-=-=-=-=-=-=-`)          ^^
         (`-=-=-=-=-=-=-`)  ^^                 ^^
     jgs   (`-=-=-=-=-`)
            `-=-=-=-=-`
         */
        matrix.pushPose();
        matrix.translate(TEXT_X - 10d, 66, 0);
        matrix.scale(0.8f, 0.8f, 0.8f);
        TERMINAL_FONT_8.draw(matrix, "     ^^      .-=-=-=-.  ^^", 6, 24, COLOR);
        TERMINAL_FONT_8.draw(matrix, " ^^        (`-=-=-=-=-`)", 6, 32, COLOR);
        TERMINAL_FONT_8.draw(matrix, "         (`-=-=-=-=-=-=-`)  ^^", 6, 40, COLOR);
        TERMINAL_FONT_8.draw(matrix, "   ^^   (`-=-=-=-=-=-=-=-`)   ^^", 6, 48, COLOR);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=(@)=-=-=-`)", 6, 56, COLOR);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 6, 64, COLOR);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)", 6, 72, COLOR);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)", 6, 80, COLOR);
        TERMINAL_FONT_8.draw(matrix, "       (`-=-=-=-=-=-=-=-=-`)  ^^", 6, 88, COLOR);
        TERMINAL_FONT_8.draw(matrix, "        (`-=-=-=-=-=-=-=-`)", 6, 96, COLOR);
        TERMINAL_FONT_8.draw(matrix, "         (`-=-=-=-=-=-=-`)  ^^", 6, 104, COLOR);
        TERMINAL_FONT_8.draw(matrix, "           (`-=-=-=-=-`)", 6, 112, COLOR);
        TERMINAL_FONT_8.draw(matrix, "            `-=-=-=-=-`", 6, 120, COLOR);
        matrix.popPose();
    }



    private void drawTerminalData(@NotNull MatrixStack matrix) {
        matrix.pushPose();
        matrix.translate(TEXT_X + 90d, 66, 0);
        String owner = centrifugeState.getOwner();
        TERMINAL_FONT_8.draw(matrix, owner, 0, 0, COLOR);
        hLine(matrix, 0, TERMINAL_FONT_8.width(owner), 6, COLOR);
        //TERMINAL_FONT_8.draw(matrix, "Activity: " + StringUtils.capitalize(centrifugeState.getCentrifugeActivity().getSerializedName()), 6, 16, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Max Tier: " + StringUtils.capitalize(centrifugeState.getMaxCentrifugeTier().getName()), 6, 24, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Energy Stored: " + centrifugeState.getEnergyStored() + "rf", 6, 32, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Energy Capacity: " + centrifugeState.getEnergyCapacity() + "rf", 6, 40, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Inputs: " + centrifugeState.getInputs().size(), 6, 48, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Item Outputs: " + centrifugeState.getItemOutputs().size(), 6, 56, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Fluid Outputs: " + centrifugeState.getFluidOutputs().size(), 6, 64, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Energy Ports: " + centrifugeState.getEnergyPorts(), 6, 72, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Voids: " + centrifugeState.getDumps().size(), 6, 80, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Gearboxes: " + centrifugeState.getGearboxes(), 6, 88, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Processors: " + centrifugeState.getProcessors(), 6, 96, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Recipe Power Modifier: " + centrifugeState.getRecipePowerModifier() * 100 + "%", 6, 104, COLOR);
        TERMINAL_FONT_8.draw(matrix, "Recipe Time Modifier: " + ModConstants.DECIMAL_FORMAT.format(centrifugeState.getRecipeTimeModifier() * 100) + "%", 6, 112, COLOR);
        matrix.popPose();
    }

    private void drawInputData(@NotNull MatrixStack matrix) {
        matrix.pushPose();
        if (minecraft != null && minecraft.level != null) {
            if (selectedEntity == null) {
                selectedEntity = (AbstractGUICentrifugeEntity) minecraft.level.getBlockEntity(CentrifugeUtils.getFromSet(centrifugeState.getInputs(), selectedBlock));
            }

            if (selectedEntity != null) {
                IReorderingProcessor text = selectedEntity.getDisplayName().getVisualOrderText();
                //Render Nav Bar
                TERMINAL_FONT_8.draw(matrix, text, 58 - TERMINAL_FONT_8.width(text) / 2f, 149, COLOR);

                //Render Info Pane
                CentrifugeInputEntity inputEntity = (CentrifugeInputEntity) selectedEntity;
                matrix.translate(TEXT_X, 66, 0);
                TERMINAL_FONT_8.draw(matrix, text, 0, 0, COLOR);
                hLine(matrix, 0, TERMINAL_FONT_8.width(text), 6, COLOR);
                TERMINAL_FONT_8.draw(matrix, "Recipe: " + inputEntity.getFilterRecipeID(), 6, 16, COLOR);
                TERMINAL_FONT_8.draw(matrix, "Processing Stage: " + inputEntity.getProcessStage(), 6, 24, COLOR);
                TERMINAL_FONT_8.draw(matrix, "Item Outputs: ", 6, 32, COLOR);
                OutputLocations<CentrifugeItemOutputEntity> itemOutputs = inputEntity.getItemOutputs();
                //TODO Loop these!
                TERMINAL_FONT_8.draw(matrix, " - Output #1: " + getOutputPos(itemOutputs, 0), 6, 40, COLOR);
                TERMINAL_FONT_8.draw(matrix, " - Output #2: " + getOutputPos(itemOutputs, 1), 6, 48, COLOR);
                TERMINAL_FONT_8.draw(matrix, " - Output #3: " + getOutputPos(itemOutputs, 2), 6, 56, COLOR);
                TERMINAL_FONT_8.draw(matrix, "Fluid Outputs: ", 6, 64, COLOR);
                OutputLocations<CentrifugeFluidOutputEntity> fluidOutputs = inputEntity.getFluidOutputs();
                TERMINAL_FONT_8.draw(matrix, " - Output #1: " + getOutputPos(fluidOutputs, 0), 6, 72, COLOR);
                TERMINAL_FONT_8.draw(matrix, " - Output #2: " + getOutputPos(fluidOutputs, 1), 6, 80, COLOR);
                TERMINAL_FONT_8.draw(matrix, " - Output #3: " + getOutputPos(fluidOutputs, 2), 6, 88, COLOR);
            }
        }
        matrix.popPose();
    }

    private String getOutputPos(OutputLocations<?> itemOutputs, int index) {
        BlockPos output = itemOutputs.get(index).getPos();
        return output == null ? "Output not linked!" : String.format("[x: %s, y: %s, z: %s]", output.getX(), output.getY(), output.getZ());
    }

    private void drawVoidData(MatrixStack matrixStack) {
    }

    private void drawFluidOutputData(MatrixStack matrixStack) {
    }

    private void drawItemOutputData(MatrixStack matrixStack) {
    }

    @Override
    @Nullable List<ITextComponent> getInfoTooltip() {
        return Lists.newArrayList(new StringTextComponent("INFO TEXT"));
    }

    @Override
    void closeScreen() {
        if (minecraft != null) minecraft.setScreen(null);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mouseAlteredX = (int) (mouseX-leftPos);
        int mouseAlteredY = (int) (mouseY-topPos);

        if (button == 0) {
            for (Rectangle tab : TABS.keySet()) {
                if (tab.contains(mouseAlteredX, mouseAlteredY)) {
                    selectedTab = tab;
                    selectedEntity = null;
                    selectedBlock = 0;
                    return true;
                }
            }
        }



/*        if (NAV_RIGHT.contains(mouseAlteredX, mouseAlteredY) && button == 0) {
            navigate(true);
            return true;
        }
        if (NAV_LEFT.contains(mouseAlteredX, mouseAlteredY) && button == 0) {
            navigate(false);
            return true;
        }*/
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void navigate(boolean navRight) {
        if (navRight) {
            selectedBlock = selectedBlock == centrifugeState.getInputs().size() -1 ? 0 : selectedBlock + 1;
        } else {
            selectedBlock = selectedBlock == 0 ? centrifugeState.getInputs().size() -1 : selectedBlock - 1;
        }

        if (minecraft != null && minecraft.level != null) {
            selectedEntity = (AbstractGUICentrifugeEntity) minecraft.level.getBlockEntity(CentrifugeUtils.getFromSet(centrifugeState.getInputs(), selectedBlock));
        }
    }
}
