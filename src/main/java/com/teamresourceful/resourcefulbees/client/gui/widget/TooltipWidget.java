package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.tooltip.AbstractTooltip;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TooltipWidget extends AbstractWidget {

    protected int xOffset = 0;
    protected int yOffset = 0;
    protected TooltipWidget parent = null;

    private List<AbstractTooltip> tooltips = new LinkedList<>();
    protected boolean mute = false;

    public TooltipWidget(int x, int y, int width, int height, Component message) {
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

    public boolean isHovered(double mouseX, double mouseY) {
        return visible && MathUtils.inRangeInclusive((int) mouseX, x + xOffset, x + width + xOffset) && MathUtils.inRangeInclusive((int) mouseY, y + yOffset, y + height + yOffset);
    }

    public void setTooltips(List<AbstractTooltip> tooltips) {
        this.tooltips = tooltips;
    }

    public List<AbstractTooltip> getTooltips() {
        return tooltips;
    }

    public void drawTooltips(@NotNull PoseStack matrix, Screen screen, int mouseX, int mouseY) {
        boolean showAdvanced = Minecraft.getInstance().options.advancedItemTooltips;
        tooltips.forEach(t -> {
            if (t.isHovered(mouseX, mouseY)) {
                screen.renderComponentTooltip(matrix, showAdvanced ? t.getAdvancedTooltip() : t.getTooltip(), mouseX, mouseY);
            }
        });
    }

    @Override
    public void renderButton(PoseStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        // do nothing please... seriously don't do anything
    }

    public boolean isClicked(double mouseX, double mouseY) {
        return this.clicked(mouseX, mouseY);
    }

    public void tick(int ticksActive) {
        // implement to use
    }

    public void playDownSound(SoundManager pHandler) {
        if (mute) return;
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
