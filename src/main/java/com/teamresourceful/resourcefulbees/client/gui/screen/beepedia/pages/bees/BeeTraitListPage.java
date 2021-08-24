package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.TraitPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees.BeeDataPage;
import com.teamresourceful.resourcefulbees.client.gui.widget.ListButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.SubButtonList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class BeeTraitListPage extends BeeDataPage {

    private SubButtonList list = null;

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (list == null) return;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.traits");
        FontRenderer font = Minecraft.getInstance().font;
        font.draw(matrix, title.withStyle(TextFormatting.WHITE), xPos, (float) yPos + 8, -1);
        list.updateList();
    }

    @Override
    public void addSearch() {
        beeData.getTraitData().getTraits().forEach(parent::addSearchTrait);
    }

    private void initList() {
        Map<String, TraitPage> traitPages = beepedia.getTraits(beeData);
        SortedMap<String, ListButton> buttons = new TreeMap<>();
        for (Map.Entry<String, TraitPage> e : traitPages.entrySet()) {
            ItemStack stack = new ItemStack(e.getValue().trait.getDisplayItem());
            TranslationTextComponent text = new TranslationTextComponent(e.getValue().trait.getTranslationKey());
            Button.IPressable onPress = button -> {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.TRAIT, e.getKey());
            };
            ListButton button = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, stack, 2, 2, text, 22, 6, onPress);
            beepedia.addButton(button);
            button.visible = false;
            buttons.put(e.getKey(), button);
        }
        list = new SubButtonList(xPos, yPos + 22, SUB_PAGE_WIDTH, SUB_PAGE_HEIGHT - 22, 21, null, buttons);
        list.setActive(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (mouseX >= xPos && mouseY >= yPos + 22 && mouseX <= xPos + SUB_PAGE_WIDTH && mouseY <= yPos + SUB_PAGE_HEIGHT) {
            list.updatePos((int) (scrollAmount * 8));
            BeepediaScreen.currScreenState.setTraitsScroll(list.getScrollPos());
            return true;
        }
        return false;
    }

    @Override
    public void openPage() {
        super.openPage();
        if (list == null) initList();
        list.setActive(true);
        list.setScrollPos(BeepediaScreen.currScreenState.getTraitsScroll());
    }

    @Override
    public void closePage() {
        super.closePage();
        if (list != null) list.setActive(false);
    }
}
