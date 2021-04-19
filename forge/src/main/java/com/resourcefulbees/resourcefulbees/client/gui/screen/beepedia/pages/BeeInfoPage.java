package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
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
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info");
        TranslatableComponent sizeName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.size");

        TranslatableComponent healthName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.health");
        TranslatableComponent damageName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage");
        TranslatableComponent stingerName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.stinger");
        TranslatableComponent passiveName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive");
        TranslatableComponent poisonName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.poison");
        TranslatableComponent timeName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.time");

        sizeName.append(BeeInfoUtils.getSizeName(beeData.getSizeModifier()));
        damageName.append(new TextComponent("" + (int) beeData.getCombatData().getAttackDamage()));
        healthName.append(new TextComponent("" + (int) beeData.getCombatData().getBaseHealth()));
        stingerName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        passiveName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().isPassive()));
        poisonName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().inflictsPoison()));
        timeName.append(beeData.getMaxTimeInHive() / 20 + "s");

        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), xPos, (float) yPos + 8, -1);
        font.draw(matrix, sizeName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 22, -1);
        font.draw(matrix, timeName.withStyle(ChatFormatting.GRAY), (float) xPos + 76, (float) yPos + 22, -1);
        font.draw(matrix, healthName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 34, -1);
        font.draw(matrix, damageName.withStyle(ChatFormatting.GRAY), (float) xPos + 76, (float) yPos + 34, -1);
        font.draw(matrix, passiveName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 46, -1);
        font.draw(matrix, poisonName.withStyle(ChatFormatting.GRAY), (float) xPos + 76, (float) yPos + 46, -1);
        font.draw(matrix, stingerName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 58, -1);
    }

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent flowerName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");
        if (beeData.hasBlockFlowers()) {
            if (!flowers.isEmpty()) {
                font.draw(matrix, flowerName.withStyle(ChatFormatting.GRAY), (float) xPos, (float) yPos + 75, -1);
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
            font.draw(matrix, flowerName.withStyle(ChatFormatting.GRAY), (float) xPos, (float) yPos + 80, -1);
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
