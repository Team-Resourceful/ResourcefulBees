package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeeDataPage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ItemMutationPage extends MutationsPage {

    List<Block> inputs;
    List<Pair<Double, ItemOutput>> outputs = new ArrayList<>();
    private Double outputChance;

    public ItemMutationPage(ITag<?> blocks, Pair<Double, RandomCollection<ItemOutput>> outputs, MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        super(type, beeData, beepedia);
        inputs = (List<Block>) blocks.values();
        initOutputs(outputs);
    }

    public ItemMutationPage(Block block, Pair<Double, RandomCollection<ItemOutput>> outputs, MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        super(type, beeData, beepedia);
        inputs = new LinkedList<>(Collections.singleton(block));
        initOutputs(outputs);
    }

    private void initOutputs(Pair<Double, RandomCollection<ItemOutput>> outputs) {
        outputChance = outputs.getKey();
        RandomCollection<ItemOutput> collection = outputs.getRight();
        collection.getMap().forEach((b, m) -> this.outputs.add(Pair.of(collection.getAdjustedWeight(m.getWeight()), m)));
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            inputCounter++;
            outputCounter++;
            if (inputCounter >= inputs.size()) inputCounter = 0;
            if (outputCounter >= outputs.size()) outputCounter = 0;
        }
    }

    @Override
    public void draw(MatrixStack matrix, int xPos, int yPos) {
        beepedia.drawSlot(matrix, inputs.get(inputCounter), xPos + 32, yPos + 32);
        ItemOutput output = outputs.get(outputCounter).getRight();
        ItemStack item = new ItemStack(output.getItem());
        if (!output.getCompoundNBT().isEmpty()) {
            item.setTag(output.getCompoundNBT());
        }
        beepedia.drawSlot(matrix, item, xPos + 112, yPos + 32);
        drawWeight(matrix, outputs.get(outputCounter).getLeft(), xPos + 122, yPos + 54);
        if (outputChance < 1){
            Minecraft.getInstance().getTextureManager().bindTexture(infoIcon);
            beepedia.drawTexture(matrix,  xPos + BeepediaPage.SUB_PAGE_WIDTH / 2 - 20, yPos + 51, 16, 0, 9, 9);
            drawChance(matrix, outputChance,xPos + BeepediaPage.SUB_PAGE_WIDTH / 2 , yPos + 52);
        }
    }

    @Override
    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
        return false;
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
        if (outputChance < 1 && BeepediaScreen.mouseHovering((float) xPos + ((float) BeeDataPage.SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 51, 8, 8, mouseX, mouseY)) {
            beepedia.renderTooltip(matrix, new TranslationTextComponent("gui.resourcefulbees.jei.category.mutation_chance.info"), mouseX, mouseY);
        }
    }
}
