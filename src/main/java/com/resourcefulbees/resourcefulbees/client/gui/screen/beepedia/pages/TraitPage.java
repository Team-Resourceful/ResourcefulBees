package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;

import java.util.stream.Collectors;

public class TraitPage extends BeepediaPage {

    private final BeeTrait trait;
    String translation;

    public TraitPage(BeepediaScreen beepedia, BeeTrait trait, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.trait = trait;
        initTranslation();
        ItemStack stack = new ItemStack(Items.BLAZE_POWDER);
        newListButton(stack, new StringTextComponent(id));
    }

    private void initTranslation() {
        translation = "";
        translation += String.join(" ", trait.getDamageImmunities().stream().map(damageSource -> damageSource.damageType).collect(Collectors.toList()));
        translation += String.join(" ", trait.getSpecialAbilities());
        translation += String.join(" ", trait.getPotionImmunities().stream().map(effect -> effect.getDisplayName().getString()).collect(Collectors.toList()));
        translation += String.join(" ", trait.getDamageTypes().stream().map(pair -> pair.getLeft()).collect(Collectors.toList()));
        translation += String.join(" ", trait.getPotionDamageEffects().stream().map(pair -> pair.getLeft().getDisplayName().getString()).collect(Collectors.toList()));
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.draw(matrix, new StringTextComponent(id), xPos, yPos + 10, Color.parse("white").getRgb());
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

    }


    @Override
    public String getSearch() {
        return translation;
    }

    @Override
    public void tick(int ticksActive) {

    }
}
