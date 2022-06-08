package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TabImageButton extends ModImageButton {

    protected final ItemStack displayItem;
    protected final int itemX;
    protected final int itemY;

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn, int textureWidth, int textureHeight, Component message) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, textureWidth, textureHeight, onPressIn, message);
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
    }

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn, int textureWidth, int textureHeight) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn, textureWidth, textureHeight, Component.empty());
    }

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn, Component message) {
        super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn, message);
        this.displayItem = displayItem;
        this.itemX = itemX;
        this.itemY = itemY;
    }

    public TabImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn, Component.empty());
    }

    @Override
    public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrix, mouseX, mouseY, partialTicks);
        if (this.displayItem != null)
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.displayItem, this.x + this.itemX, this.y + this.itemY);
        RenderSystem.enableDepthTest();
    }

}
