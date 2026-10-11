package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class BreederScreen extends BaseScreen<BreederMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("breeder/background");
    private static final Identifier ACTIVE = ModIdentifier.of("breeder/active");

    public BreederScreen(BreederMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
//        preInit();
        titleLabelX = 7;
        inventoryLabelX = 7;
        inventoryLabelY = 159;
    }

    // RID: Commented out as it was unnecessary with it being added to the constructor itself.
//    protected void preInit(){
//        this.inventoryLabelX = 30;
//        this.inventoryLabelY = 95;
//        this.titleLabelX = 30;
//    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, this.leftPos, this.topPos, 180, 256);
        for (int i = 0; i < BreederConstants.BREEDERS; i++) {
            int width = (int)(((float)menu.times.get(i) / menu.endTimes.get(i)) * 10);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 180, 0, this.leftPos + 112, this.topPos + 61, width, 2);
            if (menu.times.get(i) > 0) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ACTIVE, 4, 4, 0, 0, this.leftPos + 103, this.topPos + 60, 4, 4);
            }
        }
    }
}
