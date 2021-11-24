package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.tooltip.AbstractTooltip;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TooltipWidget extends Widget {

    protected int xOffset = 0;
    protected int yOffset = 0;
    protected TooltipWidget parent = null;

    private List<AbstractTooltip> tooltips = new LinkedList<>();
    protected boolean mute = false;

    public TooltipWidget(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
    }

    public void addParent(int xOffset, int yOffset, TooltipWidget parent) {
        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && visible && MathUtils.inRangeInclusive((int) mouseX, x + xOffset, x + width + xOffset) && MathUtils.inRangeInclusive((int) mouseY, y + yOffset, y + height + yOffset);
    }


    /**
     * @deprecated 19/11/2021, does not work with {@link Pane} Objects, use {@link TooltipWidget#isHovered(double, double)} instead.
     *
     * @return if the object is hovered.
     */
    @Deprecated
    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return visible && MathUtils.inRangeInclusive((int) mouseX, x + xOffset, x + width + xOffset) && MathUtils.inRangeInclusive((int) mouseY, y + yOffset, y + height + yOffset);
    }

    public void setTooltips(List<AbstractTooltip> tooltips) {
        this.tooltips = tooltips;
    }

    public List<AbstractTooltip> getTooltips() {
        return tooltips;
    }

    public void drawTooltips(@NotNull MatrixStack matrix, Screen screen, int mouseX, int mouseY) {
        boolean showAdvanced = Minecraft.getInstance().options.advancedItemTooltips;
        tooltips.forEach(t -> {
            if (t.isHovered(mouseX, mouseY)) {
                screen.renderComponentTooltip(matrix, showAdvanced ? t.getAdvancedTooltip() : t.getTooltip(), mouseX, mouseY);
            }
        });
    }

    @Override
    public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        // do nothing please... seriously don't do anything
    }

    public boolean isClicked(double mouseX, double mouseY) {
        return this.clicked(mouseX, mouseY);
    }

    public void tick(int ticksActive) {
        // implement to use
    }

    public void playDownSound(SoundHandler pHandler) {
        if (mute) return;
        pHandler.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
