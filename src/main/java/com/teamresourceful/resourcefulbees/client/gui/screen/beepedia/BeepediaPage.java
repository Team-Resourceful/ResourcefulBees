package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.client.gui.widget.ListButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.jline.reader.Widget;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

public abstract class BeepediaPage {

    public final BeepediaScreenArea screenArea;
    public BeepediaScreen beepedia;

    public List<Button> pageButtons;

    protected BeepediaPage(BeepediaScreenArea screenArea) {
        pageButtons = new ArrayList<>();
        this.screenArea = screenArea;
    }


    public void preInit(BeepediaScreen beepedia) {
        this.beepedia = beepedia;
    }

    public abstract void registerButtons(BeepediaScreen beepedia);

    @OverridingMethodsMustInvokeSuper
    public void openPage() {
        BeepediaScreen.setButtonsVisibility(true, pageButtons);
    }

    @OverridingMethodsMustInvokeSuper
    public void closePage() {
        BeepediaScreen.setButtonsVisibility(false, pageButtons);
    }

    public abstract void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY);

    public abstract void renderForeground(MatrixStack matrix, int mouseX, int mouseY);

    public abstract void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY);

    public abstract boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount);

    public abstract boolean mouseClicked(double mouseX, double mouseY, int mouseButton);

    public abstract void addSearch(BeepediaPage parent);

    public void tick(int ticksActive) {
        // override to implement
    }


}
