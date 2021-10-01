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

    /**
     * this will be run when the beepedia is first initialized and will never be rerun.
     * @param screenArea the screen area of the page
     */
    protected BeepediaPage(BeepediaScreenArea screenArea) {
        pageButtons = new ArrayList<>();
        this.screenArea = screenArea;
    }

    public int getXPos() {
        if (beepedia == null) return 0;
        return screenArea.getX(beepedia);
    }

    public int getYPos() {
        if (beepedia == null) return 0;
        return screenArea.getY(beepedia);
    }

    /**
     * register any data or tooltips here.
     * @param beepedia the main screen class
     */
    public void preInit(BeepediaScreen beepedia) {
        this.beepedia = beepedia;
    }

    /**
     * only register buttons here, this method will only be run when the beepedia screen is opened
     * @param beepedia the main screen class
     */
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

    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        // override to implement
    }

    @Deprecated
    public abstract void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY);

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    public abstract void addSearch(BeepediaPage parent);

    public void tick(int ticksActive) {
        // override to implement
    }


}
