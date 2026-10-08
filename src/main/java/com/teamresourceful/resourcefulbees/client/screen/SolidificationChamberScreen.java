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
import org.jspecify.annotations.NonNull;

public class SolidificationChamberScreen extends AbstractContainerScreen<SolidificationChamberMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("solidification/background");

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
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (tileEntity != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, leftPos, topPos, imageWidth, imageHeight);
        }
    }
}
