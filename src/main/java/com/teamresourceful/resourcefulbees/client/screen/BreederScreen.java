package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class BreederScreen extends AbstractContainerScreen<BreederMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("breeder/background");

    public BreederScreen(BreederMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, 198, 188);
        preInit();
    }

    protected void preInit(){
        this.inventoryLabelX = 30;
        this.inventoryLabelY = 95;
        this.titleLabelX = 30;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, this.leftPos, this.topPos, 198, 188);
        int y = this.topPos + 21;
        for (int i = 0; i < BreederConstants.BREEDERS; i++) {
            int width = (int)(((float)menu.times.get(i) / menu.endTimes.get(i)) * 118);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 246, this.leftPos + 51, y, width, 10);
            y+= 20;
        }
    }
}
