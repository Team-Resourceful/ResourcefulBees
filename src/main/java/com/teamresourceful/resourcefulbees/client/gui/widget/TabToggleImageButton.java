package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TabToggleImageButton extends TabImageButton {

    protected final ItemStack toggledItem;
    protected final int xDiffTex;
    protected boolean stateTriggered;

    public TabToggleImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int xDiffTextIn, int yDiffTextIn, boolean stateTriggered, ResourceLocation background, ItemStack displayItem, ItemStack toggledItem, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, background, displayItem, 0, 0, onPressIn);
        this.toggledItem = toggledItem;
        this.xDiffTex = xDiffTextIn;
        this.stateTriggered = stateTriggered;
    }

    public void setStateTriggered(boolean stateTriggered) { this.stateTriggered = stateTriggered; }

    public boolean isStateTriggered() { return this.stateTriggered; }

    @Override
    public void renderButton(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.xTexStart;
        int j = this.yTexStart;
        if (this.stateTriggered) {
            i += this.xDiffTex;
        }

        if (this.isHovered(mouseX, mouseY)) {
            j += this.yDiffText;
        }

        this.blit(matrixStack, this.x, this.y, i, j, this.width, this.height);
        if (this.displayItem != null && !this.stateTriggered) {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.displayItem, this.x + 1, this.y + 1);
        } else if (toggledItem != null && this.stateTriggered) {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.toggledItem, this.x + 1, this.y + 1);
        }
        RenderSystem.enableDepthTest();
    }
}
