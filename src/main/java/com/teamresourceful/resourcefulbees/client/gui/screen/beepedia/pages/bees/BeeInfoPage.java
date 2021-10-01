package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.utils.BeepediaUtils;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public class BeeInfoPage extends BeeDataPage {

    private static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info");
    private static final TranslationTextComponent SIZE_NAME = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.size");
    private static final TranslationTextComponent TIME_NAME = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.time");
    private static final TranslationTextComponent FLOWER_NAME = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");

    private Pair<EntityType<?>, Entity> entityFlower = Pair.of(null, null);
    private List<Block> blockFlowers = new LinkedList<>();
    private CustomBeeData beeData;
    private int slot = 0;

    public BeeInfoPage(BeepediaScreenArea screenArea) {
        super(screenArea);
    }

    @Override
    public void registerButtons(BeepediaScreen beepedia) {

    }

    @Override
    public void preInit(BeepediaScreen beepedia, BeepediaScreenArea screenArea, CustomBeeData beeData) {
        super.preInit(beepedia, screenArea, beeData);
        if (Minecraft.getInstance().level == null) return;
        if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
            if (!beeData.getCoreData().getBlockFlowers().containsAll(blockFlowers)) {
                blockFlowers = new LinkedList<>(beeData.getCoreData().getBlockFlowers());
                slot = 0;
            }
        } else {
            if (beeData.getCoreData().getEntityFlower().isPresent() && (entityFlower.getLeft() == null || !entityFlower.getKey().equals(beeData.getCoreData().getEntityFlower().get()))) {
                entityFlower = Pair.of(beeData.getCoreData().getEntityFlower().get(), beeData.getCoreData().getEntityFlower().get().create(Minecraft.getInstance().level));
            }
        }
    }

    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (beeData == null) throw new IllegalStateException("preInit not implemented");
        FontRenderer font = Minecraft.getInstance().font;
        SIZE_NAME.append(BeepediaUtils.getSizeName(beeData.getRenderData().getSizeModifier()));
        TIME_NAME.append(beeData.getCoreData().getMaxTimeInHive() / 20 + "s");
        font.draw(matrix, TITLE.withStyle(TextFormatting.WHITE), getXPos(), getYPos() + 8, -1);
        font.draw(matrix, SIZE_NAME.withStyle(TextFormatting.GRAY), getXPos(), getYPos() + 22, -1);
        font.draw(matrix, TIME_NAME.withStyle(TextFormatting.GRAY), getXPos() + 86, getYPos() + 22, -1);
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        if (beeData == null) throw new IllegalStateException("preInit not implemented");
        FontRenderer font = Minecraft.getInstance().font;
        if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
            font.draw(matrix, FLOWER_NAME.withStyle(TextFormatting.GRAY), getXPos(), getYPos() + 75, -1);
            beepedia.drawSlot(matrix, blockFlowers.get(slot), getXPos() + 36, getYPos() + 70);
        } else if (beeData.getCoreData().getEntityFlower().isPresent()) {
            font.draw(matrix, FLOWER_NAME.withStyle(TextFormatting.GRAY), getXPos(), getYPos() + 80, -1);
            RenderUtils.renderEntity(matrix, entityFlower.getValue(), Minecraft.getInstance().level, getXPos() + 45, getYPos() + 75, -45, 1.25f);
        }
    }

    @Override
    public void addSearch(BeepediaPage parent) {
//        if (beeData == null) throw new IllegalStateException("preInit not implemented");
//        parent.addSearchBeeTag(beeData.getCombatData().isPassive() ? "passive" : "aggressive");
//        if (beeData.getCombatData().inflictsPoison()) parent.addSearchBeeTag("poisonous");
//        if (beeData.getCombatData().removeStingerOnAttack()) parent.addSearchBeeTag("stinger");
//        if (beeData.getCoreData().getEntityFlower().isPresent()) {
//            if (entityFlower.getRight() instanceof CustomBeeEntity) {
//                parent.addSearchBee(entityFlower.getRight(), ((CustomBeeEntity) entityFlower.getRight()).getBeeType());
//            } else {
//                parent.addSearchEntity(entityFlower.getRight());
//            }
//        } else {
//            beeData.getCoreData().getBlockFlowers().forEach(parent::addSearchItem);
//        }
//        parent.addSearchBeeTag(BeepediaUtils.getSizeName(beeData.getRenderData().getSizeModifier()).getString());
    }

    @Override
    public void tick(int ticksActive) {
        if (beeData == null) throw new IllegalStateException("preInit not implemented");
        if (Screen.hasShiftDown()) return;
        if (ticksActive % 20 == 0 && !blockFlowers.isEmpty()) {
            slot++;
            if (slot > blockFlowers.size()) slot = 0;
        }
    }
}
