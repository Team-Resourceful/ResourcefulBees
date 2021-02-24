package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Map;

public class TraitListPage extends BeeDataPage {

    Map<String, TraitPage> traitPages;
    Map<String, ListButton> buttons;
    BeepediaScreen.ButtonList list;

    public TraitListPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        traitPages = beepedia.getTraits(beeData);
        buttons = new HashMap<>();
        for (Map.Entry<String, TraitPage> e : traitPages.entrySet()) {
            ItemStack stack = new ItemStack(Items.BLAZE_POWDER);
            ResourceLocation image = listImage;
            StringTextComponent text = new StringTextComponent(e.getKey());
            Button.IPressable onPress = (button) -> beepedia.setActive(BeepediaScreen.PageType.TRAIT, e.getKey());
            ListButton button = new ListButton(0, 0, 100, 20, 0, 0, 20, image, stack, 2, 2, text, 22, 6, onPress);
            beepedia.addButton(button);
            button.visible = false;
            buttons.put(e.getKey(), button);
        }
        list = new BeepediaScreen.SubButtonList(xPos, yPos + 22, subPageWidth, subPageHeight - 22, 21, null, buttons);
        list.setActive(false);
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.traits");
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        font.draw(matrix, title, xPos, yPos + 8, Color.parse("white").getRgb());
        list.updateList();
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public void tick(int ticksActive) {

    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (mouseX >= xPos && mouseY >= yPos + 22 && mouseX <= xPos + subPageWidth && mouseY <= yPos + subPageHeight) {
            list.updatePos((int) (scrollAmount * 8));
            beepedia.currScreenState.setTraitsScroll(list.scrollPos);
            return true;
        }
        return false;
    }

    @Override
    public void openPage() {
        super.openPage();
        list.setActive(true);
    }

    @Override
    public void closePage() {
        super.closePage();
        list.setActive(false);
    }
}
