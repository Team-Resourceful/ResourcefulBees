package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class BeeInfoPage extends BeeDataPage {

    private Entity entityFlower = null;
    List<Block> flowers;
    int counter;
    int size;

    public BeeInfoPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        flowers = beeData.hasBlockFlowers() ? new ArrayList<>(beeData.getBlockFlowers()) : new ArrayList<>();
        counter = 0;
        size = flowers.size();
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;

        IFormattableTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info").withStyle(TextFormatting.WHITE);
        IFormattableTextComponent sizeName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.size").withStyle(titleStyle);

        IFormattableTextComponent healthName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.health").withStyle(titleStyle);
        IFormattableTextComponent damageName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage").withStyle(titleStyle);
        IFormattableTextComponent stingerName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.stinger").withStyle(titleStyle);
        IFormattableTextComponent passiveName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive").withStyle(titleStyle);
        IFormattableTextComponent poisonName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.poison").withStyle(titleStyle);
        IFormattableTextComponent timeName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.time").withStyle(titleStyle);
        IFormattableTextComponent auraRangeName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.aura_range").withStyle(titleStyle);

        sizeName.append(BeeInfoUtils.getSizeName(beeData.getSizeModifier()).withStyle(subStyle));
        damageName.append(new StringTextComponent("" + (int) beeData.getCombatData().getAttackDamage()).withStyle(subStyle));
        healthName.append(new StringTextComponent("" + (int) beeData.getCombatData().getBaseHealth()).withStyle(subStyle));
        stingerName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()).withStyle(subStyle));
        passiveName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().isPassive()).withStyle(subStyle));
        poisonName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().inflictsPoison()).withStyle(subStyle));
        auraRangeName.append(new StringTextComponent("" + beeData.getAuraRange()).withStyle(subStyle));
        timeName.append(new StringTextComponent("" + beeData.getMaxTimeInHive() / 20 + "s").withStyle(subStyle));

        font.draw(matrix, title, xPos, (float) yPos + 8, -1);
        font.draw(matrix, sizeName, xPos, (float) yPos + 22, -1);
        font.draw(matrix, timeName, (float) xPos + 76, (float) yPos + 22, -1);
        font.draw(matrix, healthName, xPos, (float) yPos + 34, -1);
        font.draw(matrix, damageName, (float) xPos + 76, (float) yPos + 34, -1);
        font.draw(matrix, passiveName, xPos, (float) yPos + 46, -1);
        font.draw(matrix, poisonName, (float) xPos + 76, (float) yPos + 46, -1);
        font.draw(matrix, stingerName, xPos, (float) yPos + 58, -1);
        if (beeData.getTraitData().hasBeeAuras()) {
            font.draw(matrix, auraRangeName, (float) xPos + 76, (float) yPos + 70, -1);
        }
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent flowerName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");
        if (beeData.hasBlockFlowers()) {
            if (!flowers.isEmpty()) {
                font.draw(matrix, flowerName.withStyle(TextFormatting.GRAY), (float) xPos, (float) yPos + 75, -1);
                beepedia.drawSlot(matrix, flowers.get(counter), xPos + 36, yPos + 70);
            }
        } else if (beeData.hasEntityFlower()) {
            if (entityFlower == null) {
                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(beeData.getEntityFlower());
                // makes sure the entity is valid
                if (entityType.equals(EntityType.PIG) && (!beeData.getEntityFlower().equals(new ResourceLocation("minecraft:pig"))))
                    return;
                entityFlower = entityType.create(beepedia.getMinecraft().level);
            }
            font.draw(matrix, flowerName.withStyle(TextFormatting.GRAY), (float) xPos, (float) yPos + 80, -1);
            RenderUtils.renderEntity(matrix, entityFlower, beepedia.getMinecraft().level, (float) xPos + 45, (float) yPos + 75, -45, 1.25f);
        }
    }

    @Override
    public String getSearch() {
        return String.format("%s %s %s %s %s",
                BeeInfoUtils.getSizeName(beeData.getSizeModifier()).getString(),
                beeData.getFlower(),
                beeData.getCombatData().isPassive() ? "passive" : "",
                beeData.getCombatData().inflictsPoison() ? "poison" : "",
                beeData.getCombatData().removeStingerOnAttack() ? "stinger" : "");
    }

    @Override
    public void tick(int ticksActive) {
        if (BeeInfoUtils.isShiftPressed()) return;
        if (ticksActive % 20 == 0) {
            counter++;
            if (counter >= size) {
                counter = 0;
            }
        }
    }
}
