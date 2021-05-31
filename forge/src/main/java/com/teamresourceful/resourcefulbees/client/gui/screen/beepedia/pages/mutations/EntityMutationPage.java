package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.EntityOutput;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.enums.MutationType;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage.SUB_PAGE_WIDTH;

public class EntityMutationPage extends MutationsPage {

    final Entity input;
    final List<Pair<Double, EntityOutput>> outputs = new ArrayList<>();
    private Double outputChance;

    public EntityMutationPage(EntityType<?> parentEntity, BeePage parent, EntityType<?> entity, RandomCollection<EntityOutput> outputs, MutationType type, int mutationCount, BeepediaScreen beepedia) {
        this(parentEntity.create(Objects.requireNonNull(beepedia.getMinecraft().level)), parent, entity, outputs, type, mutationCount, beepedia);
    }

    public EntityMutationPage(Entity parentEntity, BeePage parent, EntityType<?> entity, RandomCollection<EntityOutput> outputs, MutationType type, int mutationCount, BeepediaScreen beepedia) {
        super(parentEntity, parent, type, mutationCount, beepedia);
        input = entity.create(beepedia.getMinecraft().level);
        initOutputs(outputs);
    }


    private void initOutputs(RandomCollection<EntityOutput> outputs) {
        outputChance = outputs.next().getChance();
        outputs.forEach(entityOutput ->  this.outputs.add(Pair.of(outputs.getAdjustedWeight(entityOutput.getWeight()), entityOutput)));
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
            outputCounter++;
            if (outputCounter >= outputs.size()) outputCounter = 0;
        }
    }

    @Override
    public void draw(PoseStack matrix, int xPos, int yPos) {
        super.draw(matrix, xPos, yPos);
        RenderUtils.renderEntity(matrix, input, beepedia.getMinecraft().level, (float) xPos + 27, (float) yPos + 32, 45, 1.25f);
        EntityOutput output = outputs.get(outputCounter).getRight();
        Entity entity = output.getGuiEntity(beepedia.getMinecraft().level);
        if (output.getCompoundNBT().isPresent()) {
            CompoundTag nbt = entity.saveWithoutId(new CompoundTag());
            nbt.merge(output.getCompoundNBT().get());
            entity.load(nbt);
        }
        RenderUtils.renderEntity(matrix, entity, beepedia.getMinecraft().level, (float) xPos + 117, (float) yPos + 32, -45, 1.25f);
        drawWeight(matrix, outputs.get(outputCounter).getLeft(), xPos + 127, yPos + 59);
        if (outputChance < 1) {
            Minecraft.getInstance().getTextureManager().bind(infoIcon);
            beepedia.blit(matrix, xPos + SUB_PAGE_WIDTH / 2 - 20, yPos + 51, 16, 0, 9, 9);
            drawChance(matrix, outputChance, xPos + SUB_PAGE_WIDTH / 2, yPos + 52);
        }
    }

    @Override
    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
        if (super.mouseClick(xPos, yPos, mouseX, mouseY)) return true;
        if (input instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) input;
            if (BeepediaScreen.mouseHovering((float) xPos + 22, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
                if (BeepediaScreen.currScreenState.getPageID().equals((beeEntity.getBeeType()))) return false;
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, beeEntity.getBeeType());
                return true;
            }
        }
        Entity output = outputs.get(outputCounter).getRight().getGuiEntity(beepedia.getMinecraft().level);
        if (output instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) output;
            if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
                if (BeepediaScreen.currScreenState.getPageID().equals(beeEntity.getBeeType())) return false;
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, beeEntity.getBeeType());
                return true;
            }
        }
        return false;
    }

    @Override
    public void drawTooltips(PoseStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
        super.drawTooltips(matrix, xPos, yPos, mouseX, mouseY);
        if (BeepediaScreen.mouseHovering((float) xPos + 22, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            MutableComponent name = input.getName().plainCopy();
            MutableComponent id = new TextComponent(input.getEncodeId()).withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(name);
            tooltip.add(id);
            beepedia.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
        } else if (BeepediaScreen.mouseHovering((float) xPos + 112, (float) yPos + 27, 30, 30, mouseX, mouseY)) {
            EntityOutput output = outputs.get(outputCounter).getRight();
            List<Component> tooltip = new ArrayList<>();
            MutableComponent name = output.getEntityType().getDescription().plainCopy();
            MutableComponent id = new TextComponent(output.getEntityType().getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(name);
            tooltip.add(id);
            if (output.getCompoundNBT().isPresent()) {
                if (Screen.hasShiftDown()) {
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
        if (input instanceof CustomBeeEntity) {
            parent.addSearchBee(input, ((CustomBeeEntity) input).getBeeType());
        } else {
            parent.addSearchEntity(input);
        }
        for (Pair<Double, EntityOutput> output : outputs) {
            if (output.getRight().getGuiEntity(beepedia.getMinecraft().level) instanceof CustomBeeEntity) {
                parent.addSearchBee(output.getRight().getGuiEntity(beepedia.getMinecraft().level), ((CustomBeeEntity) input).getBeeType());
            } else {
                parent.addSearchEntity(output.getRight().getGuiEntity(beepedia.getMinecraft().level));
            }
        }
    }
}
