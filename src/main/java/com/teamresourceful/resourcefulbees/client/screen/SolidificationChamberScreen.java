package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SolidificationChamberScreen extends AbstractContainerScreen<SolidificationChamberMenu> {

    private static final Identifier TEXTURE = ModIdentifier.of("textures/gui/solidification/solidification.png");

    private final SolidificationChamberBlockEntity tileEntity;

    public SolidificationChamberScreen(SolidificationChamberMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
        tileEntity = menu.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(TankWidget.single(leftPos + 67, topPos + 12, 14, 62, tileEntity::tankData));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}
