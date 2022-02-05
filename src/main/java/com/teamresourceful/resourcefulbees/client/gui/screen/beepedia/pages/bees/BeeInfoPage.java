package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeeInfoPage extends BeeDataPage {

    private static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info");
    private static final TranslatableComponent SIZE_NAME = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.size");
    private static final TranslatableComponent TIME_NAME = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.time");
    private static final TranslatableComponent FLOWER_NAME = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");

    private Pair<EntityType<?>, Entity> entityFlower = Pair.of(null, null);
    private List<Block> blockFlowers = new LinkedList<>();
    private CustomBeeData beeData;
    private int slot = 0;

    public BeeInfoPage(ScreenArea screenArea) {
        super(screenArea);
    }

    @Override
    public void registerScreen(BeepediaScreen beepedia) {

    }

    @Override
    public void preInit(BeepediaScreen beepedia, ScreenArea screenArea, CustomBeeData beeData) {
        super.preInit(beepedia, screenArea, beeData);
        if (Minecraft.getInstance().level == null) return;
        if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
            if (!beeData.getCoreData().getBlockFlowers().containsAll(blockFlowers)) {
                blockFlowers = new LinkedList<>(beeData.getCoreData().getBlockFlowers());
                slot = 0;
            }
        } else {
            var entityFlower = beeData.getCoreData().getEntityFlower();
            if (entityFlower.isPresent() && (this.entityFlower.getLeft() == null || !this.entityFlower.getKey().equals(entityFlower.get()))) {
                this.entityFlower = Pair.of(entityFlower.get(), entityFlower.get().create(Minecraft.getInstance().level));
            }
        }
    }

    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        if (beeData == null) throw new IllegalStateException("preInit not implemented");
        Font font = Minecraft.getInstance().font;
        SIZE_NAME.append(BeepediaUtils.getSizeName(beeData.getRenderData().sizeModifier()));
        TIME_NAME.append(beeData.getCoreData().getMaxTimeInHive() / 20 + "s");
        font.draw(matrix, TITLE.withStyle(ChatFormatting.WHITE), x, y + 8f, -1);
        font.draw(matrix, SIZE_NAME.withStyle(ChatFormatting.GRAY),x, y + 22f, -1);
        font.draw(matrix, TIME_NAME.withStyle(ChatFormatting.GRAY), x + 86f, y + 22f, -1);
    }

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        if (beeData == null) throw new IllegalStateException("preInit not implemented");
        Font font = Minecraft.getInstance().font;
        if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
            font.draw(matrix, FLOWER_NAME.withStyle(ChatFormatting.GRAY), x, y + 75f, -1);
//            beepedia.drawSlot(matrix, blockFlowers.get(slot), x + 36f, y + 70f);
        } else if (beeData.getCoreData().getEntityFlower().isPresent()) {
            font.draw(matrix, FLOWER_NAME.withStyle(ChatFormatting.GRAY), x, y + 80f, -1);
            RenderUtils.renderEntity(matrix, entityFlower.getValue(), Minecraft.getInstance().level, x + 45f, y + 75f, -45f, 1.25f);
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
