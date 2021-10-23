package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.teamresourceful.resourcefulbees.common.lib.enums.MutationType;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


@OnlyIn(Dist.CLIENT)
public class ItemMutationPage extends MutationsPage {

    final List<Block> inputs;
    final List<Pair<Double, ItemOutput>> outputs = new ArrayList<>();
    private Double outputChance;

    public ItemMutationPage(EntityType<?> bee, BeePage parent, List<Block> blocks, RandomCollection<ItemOutput> outputs, MutationType type, int mutationCount, BeepediaScreen beepedia) {
        super(bee.create(Objects.requireNonNull(beepedia.getMinecraft().level)), parent, type, mutationCount, beepedia);
        inputs = blocks;
        initOutputs(outputs);
    }

    public ItemMutationPage(Entity bee, BeePage parent, Tag<Block> blocks, RandomCollection<ItemOutput> outputs, MutationType type, int mutationCount, BeepediaScreen beepedia) {
        super(bee, parent, type, mutationCount, beepedia);
        inputs = blocks.getValues();
        initOutputs(outputs);
    }

    public ItemMutationPage(Entity bee, BeePage parent, Block block, RandomCollection<ItemOutput> outputs, MutationType type, int mutationCount, BeepediaScreen beepedia) {
        super(bee, parent, type, mutationCount, beepedia);
        inputs = new LinkedList<>(Collections.singleton(block));
        initOutputs(outputs);
    }


    private void initOutputs(RandomCollection<ItemOutput> outputs) {
        outputChance = outputs.next().getChance();
        outputs.forEach(itemOutput ->  this.outputs.add(Pair.of(outputs.getAdjustedWeight(itemOutput.getWeight()), itemOutput)));
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
            inputCounter++;
            outputCounter++;
            if (inputCounter >= inputs.size()) inputCounter = 0;
            if (outputCounter >= outputs.size()) outputCounter = 0;
        }
    }

//    @Override
//    public void draw(MatrixStack matrix, int xPos, int yPos) {
//        super.draw(matrix, xPos, yPos);
//        beepedia.drawSlot(matrix, inputs.get(inputCounter), xPos + 32, yPos + 32);
//        ItemStack item = outputs.get(outputCounter).getRight().getItemStack();
//        beepedia.drawSlot(matrix, item, xPos + 112, yPos + 32);
//        drawWeight(matrix, outputs.get(outputCounter).getLeft(), xPos + 122, yPos + 54);
//        if (outputChance < 1) {
//            Minecraft.getInstance().getTextureManager().bind(infoIcon);
//            beepedia.blit(matrix, xPos + SUB_PAGE_WIDTH / 2 - 20, yPos + 51, 16, 0, 9, 9);
//            drawChance(matrix, outputChance, xPos + SUB_PAGE_WIDTH / 2, yPos + 52);
//        }
//    }

//    @Override
//    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
//        if (super.mouseClick(xPos, yPos, mouseX, mouseY)) return true;
//        Item output = outputs.get(outputCounter).getRight().getItem();
//        if (output instanceof BeeSpawnEggItem) {
//            BeeSpawnEggItem beeEgg = (BeeSpawnEggItem) output;
//            if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
//                if (BeepediaScreen.currScreenState.getPageID().equals((beeEgg.getBeeData().toString()))) return false;
//                BeepediaScreen.saveScreenState();  //// THESE TO STRING CALLS NEED TO BE FIXED AFTER TESTING - DONT FORGET!
//                beepedia.setActive(BeepediaScreen.PageType.BEE, beeEgg.getBeeData().toString());
//                return true;
//            }
//        }
//        return false;
//    }

//    @Override
//    public void drawTooltips(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
//        super.drawTooltips(matrix, xPos, yPos, mouseX, mouseY);
//        if (outputChance < 1 && BeepediaScreen.mouseHovering((float) xPos + ((float) SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 51, 8, 8, mouseX, mouseY)) {
//            beepedia.renderTooltip(matrix, new TranslationTextComponent("gui.resourcefulbees.jei.category.mutation_chance.info"), mouseX, mouseY);
//        }
//    }

    @Override
    public void addSearch() {
//        addSearch(parent);
    }

//    public void addSearch(BeePage parent) {
//        if (entityParent instanceof CustomBeeEntity) parent.addSearchBee(entityParent.getEntity(), ((CustomBeeEntity) entityParent).getBeeType());
//        for (Block input : inputs) {
//            parent.addSearchItem(input);
//        }
//        for (Pair<Double, ItemOutput> output : outputs) {
//            parent.addSearchItem(output.getRight().getItem());
//        }
//    }
}
