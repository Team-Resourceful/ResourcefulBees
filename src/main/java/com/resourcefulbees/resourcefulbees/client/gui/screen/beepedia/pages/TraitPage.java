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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TraitPage extends BeepediaPage {

    public final BeeTrait trait;
    private final ImageButton prevTab;
    private final ImageButton nextTab;
    String translation;
    private SubButtonList list;
    TranslationTextComponent text;

    public TraitPage(BeepediaScreen beepedia, BeeTrait trait, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.trait = trait;
        initTranslation();
        text = new TranslationTextComponent(trait.getTranslationKey());
        ItemStack stack = new ItemStack(trait.getBeepediaItem());
        newListButton(stack, text);
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 40, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 40, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        beepedia.addButton(nextTab);
        beepedia.addButton(prevTab);
        nextTab.visible = false;
        prevTab.visible = false;
    }

    private void toggleTab() {
        BeepediaScreen.currScreenState.setTraitsEffectsActive(!BeepediaScreen.currScreenState.isTraitsEffectsActive());
        list.setActive(!BeepediaScreen.currScreenState.isTraitsEffectsActive());
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
        list.updateList();
        beepedia.drawSlotNoToolTip(matrix, trait.getBeepediaItem(), xPos, yPos + 10);
        beepedia.getMinecraft().textureManager.bindTexture(splitterImage);
        AbstractGui.drawTexture(matrix, xPos, yPos - 14, 0, 0, 165, 100, 165, 100);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        StringTextComponent key = new StringTextComponent(id);
        font.draw(matrix, text, (float) xPos + 24, (float) yPos + 12, TextFormatting.WHITE.getColor());
        font.draw(matrix, key, (float) xPos + 24, (float) yPos + 22, TextFormatting.DARK_GRAY.getColor());
        if (BeepediaScreen.currScreenState.isTraitsEffectsActive()) {
            drawEffectsList(matrix, xPos, yPos + 34);
        } else {
            drawBeesList(matrix, xPos, yPos + 34);
        }
    }

    private void drawBeesList(MatrixStack matrix, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.bees_list");
        int padding = font.getWidth(title) / 2;
        font.draw(matrix, title, (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, TextFormatting.WHITE.getColor());
    }

    private void drawEffectsList(MatrixStack matrix, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.effects_list");
        int padding = font.getWidth(title) / 2;
        font.draw(matrix, title, (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, TextFormatting.WHITE.getColor());
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
        list.setActive(!BeepediaScreen.currScreenState.isTraitsEffectsActive());
        list.setScrollPos(BeepediaScreen.currScreenState.getTraitBeeListPos());
        nextTab.visible = true;
        prevTab.visible = true;
    }

    @Override
    public void closePage() {
        super.closePage();
        list.setActive(false);
        nextTab.visible = false;
        prevTab.visible = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int height = 102;
        int startPos = 54;

        if (mouseX >= xPos && mouseY >= yPos + startPos && mouseX <= xPos + SUB_PAGE_WIDTH && mouseY <= yPos + startPos + height) {
            if (!BeepediaScreen.currScreenState.isTraitsEffectsActive()) {
                list.updatePos((int) (scrollAmount * 8));
                BeepediaScreen.currScreenState.setTraitBeeListPos(list.getScrollPos());
            } else {
//                int scrollPos = BeepediaScreen.currScreenState.getTraitEffectsListPos();
//                int iconHeight = 21;
//                int listHeight = effects.size() * iconHeight;
//                if (height > listHeight) return false;
//                scrollPos += scrollAmount * 8;
//                if (scrollPos > 0) scrollPos = 0;
//                else if (scrollPos < -(listHeight - height))
//                    scrollPos = -(listHeight - height);
//                BeepediaScreen.currScreenState.setTraitEffectsListPos(scrollPos);
            }
            return true;
        }
        return false;
    }
}
