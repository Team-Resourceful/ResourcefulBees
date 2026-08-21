package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.CentrifugeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CentrifugeScreen extends AbstractContainerScreen<CentrifugeMenu> {

    private static final Identifier TEXTURE = ModIdentifier.of("textures/gui/centrifuges/basic.png");

    private final CentrifugeBlockEntity tileEntity;
    private TankWidget tankWidget;

    public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
        tileEntity = menu.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void init() {
        super.init();
        tankWidget = TankWidget.selectable(leftPos + 152, topPos + 23, 16, 52, tileEntity::tankData);
        addRenderableWidget(tankWidget);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (tankWidget != null && tankWidget.isMouseOver(x, y) && tankWidget.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}