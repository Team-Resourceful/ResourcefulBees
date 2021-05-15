package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.container.MechanicalCentrifugeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MechanicalCentrifugeScreen extends AbstractContainerScreen<MechanicalCentrifugeContainer> {
    public MechanicalCentrifugeScreen(MechanicalCentrifugeContainer screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
    }
    final int textColor = 0x404040;

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture;
        texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/mechanical_centrifuge.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.menu.getCentrifugeTileEntity() != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        this.font.draw(matrix, this.title.getString(), 25, 5, textColor);
    }
}
