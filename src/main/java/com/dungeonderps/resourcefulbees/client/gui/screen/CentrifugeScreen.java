package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.container.CentrifugeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CentrifugeScreen extends ContainerScreen<CentrifugeContainer> {
    public CentrifugeScreen(CentrifugeContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
    }
    int textColor = 0x404040;
    public static String currentMonth;

    /**
     * Draws the background layer of this container (behind the items).
     *
     * @param partialTicks partial ticks
     * @param mouseX mouse x
     * @param mouseY mouse y
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture;
        if (currentMonth.equals("12")) texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/christmas_centrifuge.png");
        else texture = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/gui/centrifuges/centrifuge.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bindTexture(texture);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
            double scaledprogress = 74d * this.container.centrifugeTileEntity.time /
                    Math.max(this.container.centrifugeTileEntity.totalTime,1d);
            this.blit(i + 51, j + 28, 176, 0, (int)scaledprogress, 29);

        }
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 5, 5, textColor);
    }
}
