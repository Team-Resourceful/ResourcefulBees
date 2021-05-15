package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.BlockOutput;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.MutationTypes;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage.SUB_PAGE_WIDTH;

public class BlockMutationPage extends MutationsPage {

    final Set<Block> inputs;
    double outputChance;
    final List<Pair<Double, BlockOutput>> outputs = new ArrayList<>();

    public BlockMutationPage(Entity bee, BeePage parent, Block block, RandomCollection<BlockOutput> outputs, MutationTypes type, int mutationCount, BeepediaScreen beepedia) {
        super(bee, parent, type, mutationCount, beepedia);
        inputs = Collections.singleton(block);
        initOutputs(outputs);
    }

    private void initOutputs(RandomCollection<BlockOutput> outputs) {
        outputChance = outputs.next().getChance();
        outputs.forEach(blockOutput -> this.outputs.add(Pair.of(outputs.getAdjustedWeight(blockOutput.getWeight()), blockOutput)));
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
    public void draw(PoseStack matrix, int xPos, int yPos) {
        super.draw(matrix, xPos, yPos);
        beepedia.drawSlot(matrix, inputs.iterator().next(), xPos + 32, yPos + 32);
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
    public void drawTooltips(PoseStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
        super.drawTooltips(matrix, xPos, yPos, mouseX, mouseY);
        if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 32, 20, 20, mouseX, mouseY)) {
            BlockOutput output = outputs.get(outputCounter).getRight();
            List<Component> tooltip = new ArrayList<>();
            MutableComponent name = output.getBlock().getName();
            MutableComponent id = new TextComponent(output.getBlock().getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(name);
            tooltip.add(id);
            if (output.getCompoundNBT().isPresent()) {
                if (BeeInfoUtils.isShiftPressed()) {
                    List<String> lore = BeeInfoUtils.getLoreLines(output.getCompoundNBT().get());
                    lore.forEach(l -> tooltip.add(new TextComponent(l).withStyle(Style.EMPTY.withColor(TextColor.parseColor("dark_purple")))));
                } else {
                    tooltip.add(new TranslatableComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(TextColor.parseColor("dark_purple"))));
                }
            }
            beepedia.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
        }
        if (outputChance < 1 && BeepediaScreen.mouseHovering((float) xPos + ((float) SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 51, 8, 8, mouseX, mouseY)) {
            beepedia.renderTooltip(matrix, new TranslatableComponent("gui.resourcefulbees.jei.category.mutation_chance.info"), mouseX, mouseY);
        }
    }

    @Override
    public void addSearch() {
        addSearch(parent);
    }

    public void addSearch(BeePage parent) {
        if (entityParent instanceof CustomBeeEntity) parent.addSearchBee(entityParent.getEntity(), ((CustomBeeEntity) entityParent).getBeeType());
        if (parent == null) return;
        for (Block input : inputs) {
            parent.addSearchItem(input);
        }
        for (Pair<Double, BlockOutput> output : outputs) {
            parent.addSearchItem(output.getRight().getBlock());
        }
    }
}
