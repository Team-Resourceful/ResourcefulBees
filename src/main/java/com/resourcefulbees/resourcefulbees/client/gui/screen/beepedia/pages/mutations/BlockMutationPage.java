package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.BlockOutput;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage.SUB_PAGE_WIDTH;

public class BlockMutationPage extends MutationsPage {

    List<Block> inputs;
    double outputChance;
    List<Pair<Double, BlockOutput>> outputs = new ArrayList<>();

    public BlockMutationPage(BeePage parent, ITag<?> blocks, Pair<Double, RandomCollection<BlockOutput>> outputs, MutationTypes type, int mutationCount, BeepediaScreen beepedia) {
        super(parent, type, mutationCount, beepedia);
        if (blocks.getValues().get(0) instanceof Fluid) {
            inputs = blocks.getValues().stream().map(f -> ((Fluid) f).defaultFluidState().createLegacyBlock().getBlock()).distinct().collect(Collectors.toList());
        } else {
            inputs = (List<Block>) blocks.getValues();
        }
        initOutputs(outputs);
    }

    public BlockMutationPage(BeePage parent, Block block, Pair<Double, RandomCollection<BlockOutput>> outputs, MutationTypes type, int mutationCount, BeepediaScreen beepedia) {
        super(parent, type, mutationCount, beepedia);
        inputs = new LinkedList<>(Collections.singleton(block));
        initOutputs(outputs);
    }

    private void initOutputs(Pair<Double, RandomCollection<BlockOutput>> outputs) {
        outputChance = outputs.getKey();
        RandomCollection<BlockOutput> collection = outputs.getRight();
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
        super.draw(matrix, xPos, yPos);
        beepedia.drawSlot(matrix, inputs.get(inputCounter), xPos + 32, yPos + 32);
        beepedia.drawSlotNoToolTip(matrix, outputs.get(outputCounter).getRight().getBlock(), xPos + 112, yPos + 32);
        drawWeight(matrix, outputs.get(outputCounter).getLeft(), xPos + 122, yPos + 54);
        if (outputChance < 1) {
            Minecraft.getInstance().getTextureManager().bind(infoIcon);
            beepedia.blit(matrix, xPos + SUB_PAGE_WIDTH / 2 - 20, yPos + 51, 16, 0, 9, 9);
            drawChance(matrix, outputChance, xPos + SUB_PAGE_WIDTH / 2, yPos + 52);
        }
    }

    @Override
    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
        return super.mouseClick(xPos, yPos, mouseX, mouseY);
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
        super.drawTooltips(matrix, xPos, yPos, mouseX, mouseY);
        if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 32, 20, 20, mouseX, mouseY)) {
            BlockOutput output = outputs.get(outputCounter).getRight();
            List<ITextComponent> tooltip = new ArrayList<>();
            IFormattableTextComponent name = output.getBlock().getName();
            IFormattableTextComponent id = new StringTextComponent(output.getBlock().getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY);
            tooltip.add(name);
            tooltip.add(id);
            if (!output.getCompoundNBT().isEmpty()) {
                if (BeeInfoUtils.isShiftPressed()) {
                    List<String> lore = BeeInfoUtils.getLoreLines(output.getCompoundNBT());
                    lore.forEach(l -> tooltip.add(new StringTextComponent(l).withStyle(Style.EMPTY.withColor(Color.parseColor("dark_purple")))));
                } else {
                    tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(Color.parseColor("dark_purple"))));
                }
            }
            beepedia.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
        }
        if (outputChance < 1 && BeepediaScreen.mouseHovering((float) xPos + ((float) SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 51, 8, 8, mouseX, mouseY)) {
            beepedia.renderTooltip(matrix, new TranslationTextComponent("gui.resourcefulbees.jei.category.mutation_chance.info"), mouseX, mouseY);
        }
    }

    @Override
    public String getSearch() {
        String search = "";
        for (Block input : inputs) {
            search = String.format("%s %s", search, input.getName().getString());
        }
        for (Pair<Double, BlockOutput> output : outputs) {
            search = String.format("%s %s", search, output.getRight().getBlock().getName().getString());
        }
        return search;
    }
}
