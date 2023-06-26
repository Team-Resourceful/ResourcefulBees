package com.teamresourceful.resourcefulbees.centrifuge.client.components;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons.NavButton;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.OutputLocationSelectionPacket;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OutputLocationSelectionWidget extends ParentWidget {

    private static final ItemStack NULL_OUTPUT_ICON = Items.BARRIER.getDefaultInstance();

    private final CentrifugeInputEntity inputEntity;
    private final int recipeOutputSlot;
    private final CentrifugeOutputType outputType;
    private final Component outputSlot;
    private final List<BlockPos> outputsList;

    //todo: consider caching the output tile and pos and changing only when updated
    public OutputLocationSelectionWidget(int x, int y, CentrifugeInputEntity inputEntity, int recipeOutputSlot, CentrifugeOutputType outputType, List<BlockPos> outputsList) {
        super(x, y);
        this.inputEntity = inputEntity;
        this.recipeOutputSlot = recipeOutputSlot;
        this.outputType = outputType;
        this.outputsList = outputsList;
        this.outputSlot = Component.translatable(CentrifugeTranslations.OUTPUT_SLOT, recipeOutputSlot + 1);
        init();
    }

    @Override
    protected void init() {
        addRenderableWidget(new NavButton(x + 52, y + 23, false, () -> switchOutput(false)));
        addRenderableWidget(new NavButton(x + 17, y + 23, true, () -> switchOutput(true)));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (inputEntity == null) return;
        graphics.blit(CentrifugeTextures.COMPONENTS, x + 29, y + 20, 0, outputType.isItem() ? 36 : 0, 18, 18);

        var tile = inputEntity.getOutputLocationGroup(outputType).get(recipeOutputSlot).getTile();
        graphics.renderItem(tile == null ? NULL_OUTPUT_ICON : tile.getBlockState().getBlock().asItem().getDefaultInstance(), x + 30, y + 21);

        TextUtils.tf8DrawCenteredStringNoShadow(graphics, outputSlot, x + 38, y, TextUtils.FONT_COLOR_1);

        BlockPos blockPos = inputEntity.getOutputLocationGroup(outputType).get(recipeOutputSlot).getPos();
        drawOutputPos(graphics, blockPos == null ? CentrifugeTranslations.OUTPUT_NOT_LINKED : Component.literal(CentrifugeUtils.formatBlockPos(blockPos)));
    }

    private void drawOutputPos(GuiGraphics graphics, Component pos) {
        TextUtils.tf8DrawCenteredStringNoShadow(graphics, pos, x + 38, y + 55, TextUtils.FONT_COLOR_1);
    }

    private void switchOutput(boolean reverse) {
        BlockPos blockPos = inputEntity.getOutputLocationGroup(outputType).get(recipeOutputSlot).getPos();
        int outputIndex = outputsList.indexOf(blockPos);
        int newOutputIndex = outputIndex == -1 ? 0 : rotateSelection(outputIndex, outputsList.size() - 1, reverse);
        if (outputIndex != newOutputIndex) {
            NetworkHandler.CHANNEL.sendToServer(new OutputLocationSelectionPacket(outputType, recipeOutputSlot, outputsList.get(newOutputIndex), inputEntity.getBlockPos()));
        }
    }

    private int rotateSelection(int current, int max, boolean reverse) {
        if (reverse) {
            return current == 0 ? max : --current;
        }
        return current == max ? 0 : ++current;
    }
}
