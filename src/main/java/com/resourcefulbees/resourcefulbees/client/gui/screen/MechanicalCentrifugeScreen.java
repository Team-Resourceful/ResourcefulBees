package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.MechanicalCentrifugeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MechanicalCentrifugeScreen extends ContainerScreen<MechanicalCentrifugeContainer> {
    public MechanicalCentrifugeScreen(MechanicalCentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }
    int textColor = 0x404040;

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture;
        texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/mechanical_centrifuge.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bindTexture(texture);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getString(), 25, 5, textColor);
    }
}
