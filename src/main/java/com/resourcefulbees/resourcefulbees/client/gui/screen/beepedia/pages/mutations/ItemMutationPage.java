package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.tags.ITag;
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
        outputs.getRight().getMap().forEach((b, m) -> this.outputs.add(Pair.of(b, m)));
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
        beepedia.drawSlot(matrix, inputs.get(inputCounter), xPos, yPos);
        beepedia.drawSlot(matrix, outputs.get(outputCounter).getRight().getItem(), xPos + 40, yPos);
    }

    @Override
    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
        return false;
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {

    }
}
