package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.terminal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.AbstractDisplayModule;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.AbstractTerminalModule;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules.DisplayTab;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeFluidOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.OutputLocations;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static com.teamresourceful.resourcefulbees.common.utils.RenderUtils.TERMINAL_FONT_12;
import static com.teamresourceful.resourcefulbees.common.utils.RenderUtils.TERMINAL_FONT_8;

public class TerminalInputsModule extends AbstractTerminalModule<CentrifugeTerminalScreen> {

    //TODO change these to translation texts
    private static final Component HOME_TEXT = new TextComponent("Home");
    private static final Component ITEM_OUTS_TEXT = new TextComponent("Item Outputs");
    private static final Component FLUID_OUTS_TEXT = new TextComponent("Fluid Outputs");

    //Nav Buttons
    //TODO make these a 3 texture "button" or modify DisplayTab to handle such cases by passing in uv values
    private final DisplayTab navRight = new DisplayTab(86, 143, 9, 13, screen);
    private final DisplayTab navLeft = new DisplayTab(24, 143, 9, 13, screen);
    private int selectedBlock = 0;
    private final Set<BlockPos> navList = screen.getCentrifugeState().getInputs();
    private @Nullable AbstractGUICentrifugeEntity selectedEntity;

    //Display Tabs
    private final DisplayTab homeTab = new DisplayTab(24, 159, 69, 13, HOME_TEXT, screen);
    private final DisplayTab itemOutsTab = new DisplayTab(24, 173, 69, 13, ITEM_OUTS_TEXT, screen);
    private final DisplayTab fluidOutsTab = new DisplayTab(24, 187, 69, 13, FLUID_OUTS_TEXT, screen);
    //Display Modules
    private final HomeModule homeModule = new HomeModule(102, 39, 237, 164, this, screen);
    private final OutputsModule itemOutputsModule = new OutputsModule(102, 39, 237, 164, this, screen);
    private final OutputsModule fluidOutputsModule = new OutputsModule(102, 39, 237, 164, this, screen);

    private final Map<DisplayTab, AbstractDisplayModule<?>> tabs = Map.of(homeTab, homeModule, itemOutsTab, itemOutputsModule, fluidOutsTab, fluidOutputsModule);
    private Map.Entry<DisplayTab, AbstractDisplayModule<?>> loadedModule = Pair.of(homeTab, homeModule);

    public TerminalInputsModule(CentrifugeTerminalScreen screen) {
        super(screen);
        homeTab.setSelected(true);
        selectedEntity = Minecraft.getInstance().level != null ? (AbstractGUICentrifugeEntity) Minecraft.getInstance().level.getBlockEntity(CentrifugeUtils.getFromSet(navList, selectedBlock)) : null;
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        //Draw Info Panel Title Bar
        blit(matrix, screen.getGuiLeft() + 104, screen.getGuiTop() + 55, 21, 0, 233, 3);
        //Draw nav "module"
        blit(matrix, screen.getGuiLeft() + 21, screen.getGuiTop() + 140, 0, 193, 75, 63);
        tabs.keySet().forEach(tab -> tab.renderBackground(matrix, partialTicks, mouseX, mouseY));
        loadedModule.getValue().renderBackground(matrix, partialTicks, mouseX, mouseY);
    }

    @Override
    public void renderText(PoseStack matrix, int mouseX, int mouseY) {
        //TODO make this a translation component
        String text = "Input #" + (selectedBlock + 1);
        TERMINAL_FONT_12.draw(matrix, text, 58f - TERMINAL_FONT_12.width(text)/2f, 146.5f, RenderUtils.FONT_COLOR_1);
        tabs.keySet().forEach(tab -> tab.renderText(matrix, mouseX, mouseY));
        loadedModule.getValue().renderText(matrix, mouseX, mouseY);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        for (Map.Entry<DisplayTab, AbstractDisplayModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onMouseClicked(mouseX, mouseY, button)) {
                loadedModule.getKey().setSelected(false);
                loadedModule = entry;
                loadedModule.getKey().setSelected(true);
                return true;
            }
        }
        if (navRight.onMouseClicked(mouseX, mouseY, button)) {
            navigate(true);
            return true;
        }
        if (navLeft.onMouseClicked(mouseX, mouseY, button)) {
            navigate(false);
            return true;
        }
        return loadedModule.getValue().onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean onCharTyped(char pCodePoint, int pModifiers) {
        for (Map.Entry<DisplayTab, AbstractDisplayModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onCharTyped(pCodePoint, pModifiers)) return true;
        }
        return loadedModule.getValue().onCharTyped(pCodePoint, pModifiers) || super.onCharTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean onKeyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for (Map.Entry<DisplayTab, AbstractDisplayModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onKeyPressed(pKeyCode, pScanCode, pModifiers)) return true;
        }
        return loadedModule.getValue().onKeyPressed(pKeyCode, pScanCode, pModifiers) || super.onKeyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean onMouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (Map.Entry<DisplayTab, AbstractDisplayModule<?>> entry: tabs.entrySet()) {
            if (entry.getKey().onMouseScrolled(pMouseX, pMouseY, pDelta)) return true;
        }
        return loadedModule.getValue().onMouseScrolled(pMouseX, pMouseY, pDelta) || super.onMouseScrolled(pMouseX, pMouseY, pDelta);
    }

    private void navigate(boolean navRight) {
        if (!navList.isEmpty()) {
            if (navRight) {
                selectedBlock = selectedBlock == navList.size() - 1 ? 0 : selectedBlock + 1;
            } else {
                selectedBlock = selectedBlock == 0 ? navList.size() - 1 : selectedBlock - 1;
            }

            if (screen.getMinecraft().level != null) {
                selectedEntity = (AbstractGUICentrifugeEntity) screen.getMinecraft().level.getBlockEntity(CentrifugeUtils.getFromSet(navList, selectedBlock));
            }
        }
    }

    private static class HomeModule extends AbstractDisplayModule<CentrifugeTerminalScreen> {

        private final TerminalInputsModule module;

        protected HomeModule(int x, int y, int width, int height, TerminalInputsModule module, CentrifugeTerminalScreen screen) {
            super(x, y, width, height, screen);
            this.module = module;
        }

        private String getOutputPos(OutputLocations<?> itemOutputs, int index) {
            BlockPos output = itemOutputs.get(index).getPos();
            //TODO make this translatable
            return output == null ? "Output not linked!" : CentrifugeUtils.formatBlockPos(output);
        }

        @Override
        public void renderText(PoseStack matrix, int mouseX, int mouseY) {
            if (module.selectedEntity != null) {
                TERMINAL_FONT_12.draw(matrix, module.selectedEntity.getDisplayName(), 220 - TERMINAL_FONT_12.width(module.selectedEntity.getDisplayName()) / 2f, 45.5f, RenderUtils.FONT_COLOR_1);

                //Render Info Pane
                CentrifugeInputEntity inputEntity = (CentrifugeInputEntity) module.selectedEntity;
                //matrix.translate(x + 8, y + 28, 0);
                int tX = x + 14;
                int tY = y + 14;
                //TODO make these translatable texts
                TERMINAL_FONT_8.draw(matrix, "Location: " + CentrifugeUtils.formatBlockPos(inputEntity.getBlockPos()), tX + 6f, tY + 16f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, "Recipe: " + inputEntity.getFilterRecipeID(), tX + 6f, tY + 24f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, "Processing Stage: " + inputEntity.getProcessStage(), tX + 6f, tY + 32f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, "Item Outputs: ", tX + 6f, tY + 40f, RenderUtils.FONT_COLOR_1);
                OutputLocations<CentrifugeItemOutputEntity> itemOutputs = inputEntity.getItemOutputs();
                //TODO Loop these!
                TERMINAL_FONT_8.draw(matrix, " - Output #1: " + getOutputPos(itemOutputs, 0), tX + 6f, tY + 48f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, " - Output #2: " + getOutputPos(itemOutputs, 1), tX + 6f, tY + 56f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, " - Output #3: " + getOutputPos(itemOutputs, 2), tX + 6f, tY + 64f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, "Fluid Outputs: ", tX + 6f, tY + 72f, RenderUtils.FONT_COLOR_1);
                OutputLocations<CentrifugeFluidOutputEntity> fluidOutputs = inputEntity.getFluidOutputs();
                TERMINAL_FONT_8.draw(matrix, " - Output #1: " + getOutputPos(fluidOutputs, 0), tX + 6f, tY + 80f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, " - Output #2: " + getOutputPos(fluidOutputs, 1), tX + 6f, tY + 88f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, " - Output #3: " + getOutputPos(fluidOutputs, 2), tX + 6f, tY + 96f, RenderUtils.FONT_COLOR_1);
            }
        }
    }

    private static class OutputsModule extends AbstractDisplayModule<CentrifugeTerminalScreen> {

        private final TerminalInputsModule module;

        protected OutputsModule(int x, int y, int width, int height, TerminalInputsModule module, CentrifugeTerminalScreen screen) {
            super(x, y, width, height, screen);
            this.module = module;
        }

        @Override
        public void renderBackground(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
/*            int tX = screen.getGuiLeft() + x + 110;
            int tY = screen.getGuiTop() + y + 38;
            Set<BlockPos> outputBlocks = screen.getCentrifugeState().getItemOutputs();
            int i = 0;
            if (module.selectedEntity != null) {
                for (BlockPos pos : outputBlocks) {
                    for (int j = 0; j < 3; j++) {
                        int uOffset = ((CentrifugeInputEntity) module.selectedEntity).getItemOutputs().get(j).getPos().equals(pos) ? 53 : 35;
                        blit(matrix, tX + j * 11, tY + i * 20, uOffset, 165, 9, 9);
                    }
                    i++;
                }
            }*/
        }

        @Override
        public void renderText(PoseStack matrix, int mouseX, int mouseY) {
            if (module.selectedEntity != null) {
                TERMINAL_FONT_12.draw(matrix, module.selectedEntity.getDisplayName(), 220f - TERMINAL_FONT_12.width(module.selectedEntity.getDisplayName()) / 2f, 45.5f, RenderUtils.FONT_COLOR_1);

                //Render Info Pane
                //CentrifugeInputEntity inputEntity = (CentrifugeInputEntity) module.selectedEntity;
                int tX = x + 5;
                int tY = y + 36;

                TERMINAL_FONT_8.draw(matrix, String.valueOf(1), x+113f-25f, y+24f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, String.valueOf(2), x+124f-25f, y+24f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, String.valueOf(3), x+135f-25f, y+24f, RenderUtils.FONT_COLOR_1);

                Set<BlockPos> outputBlocks = screen.getCentrifugeState().getItemOutputs();
                int i = 0;
                for (BlockPos pos: outputBlocks) {
                    AbstractGUICentrifugeEntity outputTile = (AbstractGUICentrifugeEntity) screen.getMinecraft().level.getBlockEntity(pos);
                    TERMINAL_FONT_8.draw(matrix, outputTile.getDisplayName(), tX, tY + i*20f, RenderUtils.FONT_COLOR_1);
                    TERMINAL_FONT_8.draw(matrix, CentrifugeUtils.formatBlockPos(pos), tX, tY + 8f + i*20f, RenderUtils.FONT_COLOR_1);

                    for (int j = 0; j < 3; j++) {
                        String text = ((CentrifugeInputEntity) module.selectedEntity).getItemOutputs().get(j).getPos().equals(pos) ? "[✗]" : "[ ]";
                        TERMINAL_FONT_8.draw(matrix, text, tX + 80f + j * 11f, tY + i * 20f, RenderUtils.FONT_COLOR_1);



                        //int uOffset = ((CentrifugeInputEntity) module.selectedEntity).getItemOutputs().get(j).getPos().equals(pos) ? 53 : 35;


                        //blit(matrix, tX + 80 + j * 11, tY + i * 20, uOffset, 165, 9, 9);
                    }

                    i++;
                }

                tX += 125;

                TERMINAL_FONT_8.draw(matrix, String.valueOf(1), x+263f-50, y+24f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, String.valueOf(2), x+274f-50, y+24f, RenderUtils.FONT_COLOR_1);
                TERMINAL_FONT_8.draw(matrix, String.valueOf(3), x+285f-50, y+24f, RenderUtils.FONT_COLOR_1);

                i = 0;
                for (BlockPos pos: outputBlocks) {
                    AbstractGUICentrifugeEntity outputTile = (AbstractGUICentrifugeEntity) screen.getMinecraft().level.getBlockEntity(pos);
                    TERMINAL_FONT_8.draw(matrix, outputTile.getDisplayName(), tX, tY + i*20f, RenderUtils.FONT_COLOR_1);
                    TERMINAL_FONT_8.draw(matrix, CentrifugeUtils.formatBlockPos(pos), tX, tY + 8f + i*20f, RenderUtils.FONT_COLOR_1);

                    for (int j = 0; j < 3; j++) {
                        String text = ((CentrifugeInputEntity) module.selectedEntity).getItemOutputs().get(j).getPos().equals(pos) ? "[✗]" : "[ ]";
                        TERMINAL_FONT_8.draw(matrix, text, tX + 80f + j * 11, tY + i * 20f, RenderUtils.FONT_COLOR_1);



                        //int uOffset = ((CentrifugeInputEntity) module.selectedEntity).getItemOutputs().get(j).getPos().equals(pos) ? 53 : 35;


                        //blit(matrix, tX + 80 + j * 11, tY + i * 20, uOffset, 165, 9, 9);
                    }

                    i++;
                }
            }
        }
    }
}
