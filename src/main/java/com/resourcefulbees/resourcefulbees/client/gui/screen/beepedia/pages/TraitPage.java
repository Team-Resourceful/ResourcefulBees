package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ListButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.SubButtonList;
import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TraitPage extends BeepediaPage {

    private final BeeTrait trait;
    private Map<String, BeePage> beePages;
    private TreeMap<String, ListButton> buttons;
    String translation;
    private SubButtonList list;

    public TraitPage(BeepediaScreen beepedia, BeeTrait trait, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.trait = trait;
        initTranslation();
        ItemStack stack = new ItemStack(Items.BLAZE_POWDER);
        newListButton(stack, new StringTextComponent(id));
    }

    private void initTranslation() {
        translation = "";
        translation += trait.getDamageImmunities().stream().map(damageSource -> damageSource.damageType).collect(Collectors.joining(" "));
        translation += String.join(" ", trait.getSpecialAbilities());
        translation += trait.getPotionImmunities().stream().map(effect -> effect.getDisplayName().getString()).collect(Collectors.joining(" "));
        translation += trait.getDamageTypes().stream().map(Pair::getLeft).collect(Collectors.joining(" "));
        translation += trait.getPotionDamageEffects().stream().map(pair -> pair.getLeft().getDisplayName().getString()).collect(Collectors.joining(" "));
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (list == null) return;
        Minecraft.getInstance().fontRenderer.draw(matrix, new StringTextComponent(id), xPos, (float) yPos + 10f, TextFormatting.WHITE.getColor());
    }

    private void initList() {
        Map<String, BeePage> beePages = beepedia.getBees(id);
        SortedMap<String, ListButton> buttons = new TreeMap<>();
        for (Map.Entry<String, BeePage> e : beePages.entrySet()) {
            ItemStack stack = new ItemStack(ModItems.BEE_JAR.get());
            BeeJar.fillJar(stack, e.getValue().beeData);
            ResourceLocation image = listImage;
            ITextComponent translation = e.getValue().beeData.getTranslation();
            Button.IPressable onPress = button -> {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, e.getKey());
            };
            ListButton button = new ListButton(0, 0, 100, 20, 0, 0, 20, image, stack, 2, 2, translation, 22, 6, onPress);
            beepedia.addButton(button);
            button.visible = false;
            buttons.put(e.getKey(), button);
        }
        list = new SubButtonList(xPos, yPos + 54, SUB_PAGE_WIDTH, 102, 21, null, buttons);
        list.setActive(false);
    }

    @Override
    public String getSearch() {
        return translation;
    }

    @Override
    public void openPage() {
        super.openPage();
        if (list == null) initList();
    }

    @Override
    public void closePage() {
        super.closePage();
    }
}
