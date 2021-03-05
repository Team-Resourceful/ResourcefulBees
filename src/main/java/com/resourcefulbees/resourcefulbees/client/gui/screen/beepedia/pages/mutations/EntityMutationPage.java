package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeeDataPage;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class EntityMutationPage extends MutationsPage {

    Entity input;
    List<Pair<Double, EntityOutput>> outputs = new ArrayList<>();
    private Double outputChance;

    public EntityMutationPage(EntityType<?> entity, Pair<Double, RandomCollection<EntityOutput>> outputs, MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        super(type, beeData, beepedia);
        input = entity.create(beepedia.getMinecraft().world);
        initOutputs(outputs);
    }

    private void initOutputs(Pair<Double, RandomCollection<EntityOutput>> outputs) {
        outputChance = outputs.getKey();
        RandomCollection<EntityOutput> collection = outputs.getRight();
        collection.getMap().forEach((b, m) -> this.outputs.add(Pair.of(collection.getAdjustedWeight(m.getWeight()), m)));
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            outputCounter++;
            if (outputCounter >= outputs.size()) outputCounter = 0;
        }
    }

    @Override
    public void draw(MatrixStack matrix, int xPos, int yPos) {
        RenderUtils.renderEntity(matrix, input, beepedia.getMinecraft().world, (float) xPos + 27, (float) yPos + 32, 45, 1.25f);
        EntityOutput output = outputs.get(outputCounter).getRight();
        Entity entity = output.getGuiEntity(beepedia.getMinecraft().world);
        if (!output.getCompoundNBT().isEmpty()) {
            CompoundNBT nbt = entity.writeWithoutTypeId(new CompoundNBT());
            nbt.merge(output.getCompoundNBT());
            entity.read(nbt);
        }
        RenderUtils.renderEntity(matrix, entity, beepedia.getMinecraft().world, (float) xPos + 117, (float) yPos + 32, -45, 1.25f);
        drawWeight(matrix, outputs.get(outputCounter).getLeft(), xPos + 127, yPos + 59);
        if (outputChance < 1) {
            Minecraft.getInstance().getTextureManager().bindTexture(infoIcon);
            beepedia.drawTexture(matrix, xPos + BeepediaPage.SUB_PAGE_WIDTH / 2 - 20, yPos + 51, 16, 0, 9, 9);
            drawChance(matrix, outputChance, xPos + BeepediaPage.SUB_PAGE_WIDTH / 2, yPos + 52);
        }
    }

    @Override
    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
        if (input instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) input;
            if (BeepediaScreen.mouseHovering((float) xPos + 22, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, beeEntity.getBeeData().getName());
                return true;
            }
        }
        Entity output = outputs.get(outputCounter).getRight().getGuiEntity(beepedia.getMinecraft().world);
        if (output instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) output;
            if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, beeEntity.getBeeData().getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
        if (BeepediaScreen.mouseHovering((float) xPos + 22, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
            List<ITextComponent> tooltip = new ArrayList<>();
            IFormattableTextComponent name = input.getName().copy();
            IFormattableTextComponent id = new StringTextComponent(input.getEntityString()).formatted(TextFormatting.DARK_GRAY);
            tooltip.add(name);
            tooltip.add(id);
            beepedia.renderTooltip(matrix, tooltip, mouseX, mouseY);
        } else if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
            EntityOutput output = outputs.get(outputCounter).getRight();
            List<ITextComponent> tooltip = new ArrayList<>();
            IFormattableTextComponent name = output.getEntityType().getName().copy();
            IFormattableTextComponent id = new StringTextComponent(output.getEntityType().getRegistryName().toString()).formatted(TextFormatting.DARK_GRAY);
            tooltip.add(name);
            tooltip.add(id);
            if (!output.getCompoundNBT().isEmpty()) {
                if (BeeInfoUtils.isShiftPressed()) {
                    List<String> lore = BeeInfoUtils.getLoreLines(output.getCompoundNBT());
                    lore.forEach(l -> tooltip.add(new StringTextComponent(l).fillStyle(Style.EMPTY.withColor(Color.parse("dark_purple")))));
                } else {
                    tooltip.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt").fillStyle(Style.EMPTY.withColor(Color.parse("dark_purple"))));
                }
            }
            beepedia.renderTooltip(matrix, tooltip, mouseX, mouseY);
        }
        if (outputChance < 1 && BeepediaScreen.mouseHovering((float) xPos + ((float) BeeDataPage.SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 51, 8, 8, mouseX, mouseY)) {
            beepedia.renderTooltip(matrix, new TranslationTextComponent("gui.resourcefulbees.jei.category.mutation_chance.info"), mouseX, mouseY);
        }
    }
}
