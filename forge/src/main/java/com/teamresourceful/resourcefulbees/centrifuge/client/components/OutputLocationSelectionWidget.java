package com.teamresourceful.resourcefulbees.centrifuge.client.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons.NavButton;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.CentrifugeInputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.OutputLocationSelectionPacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefullib.client.components.ParentWidget;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OutputLocationSelectionWidget extends ParentWidget {

    private static final ItemStack NULL_OUTPUT_ICON = Items.BARRIER.getDefaultInstance();
    private static final Component NULL_OUTPUT_POS = Component.literal("Output Not Linked!");

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
        this.outputSlot = Component.literal("Output " + (recipeOutputSlot + 1));
        init();
    }

    @Override
    protected void init() {
        addRenderableWidget(new NavButton(x+52, y+23, false, () -> switchOutput(false)));
        addRenderableWidget(new NavButton(x+17, y+23, true, () -> switchOutput(true)));
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (inputEntity == null) return;
        RenderUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x+29, y+20, 0, outputType.isItem() ? 36 : 0, 18, 18);

        var tile =  inputEntity.getOutputLocationGroup(outputType).get(recipeOutputSlot).getTile();
        drawOutputItem(tile == null ? NULL_OUTPUT_ICON : tile.getBlockState().getBlock().asItem().getDefaultInstance());

        TextUtils.tf8DrawCenteredStringNoShadow(stack, outputSlot, x+38.5f, y, TextUtils.FONT_COLOR_1);

        BlockPos blockPos = inputEntity.getOutputLocationGroup(outputType).get(recipeOutputSlot).getPos();
        drawOutputPos(stack, blockPos == null ? NULL_OUTPUT_POS : Component.literal(CentrifugeUtils.formatBlockPos(blockPos)));
    }

    private void drawOutputItem(ItemStack itemStack) {
        Minecraft.getInstance().getItemRenderer().renderGuiItem(itemStack, x+30, y+21);
    }

    private void drawOutputPos(PoseStack stack, Component pos) {
        TextUtils.tf8DrawCenteredStringNoShadow(stack, pos, x+38.5f, y+55f, TextUtils.FONT_COLOR_1);
    }

    private void switchOutput(boolean reverse) {
        BlockPos blockPos = inputEntity.getOutputLocationGroup(outputType).get(recipeOutputSlot).getPos();
        int outputIndex = outputsList.indexOf(blockPos);
        int newOutputIndex = outputIndex == -1 ? 0 : rotateSelection(outputIndex, outputsList.size()-1, reverse);
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
