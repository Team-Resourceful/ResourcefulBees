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
import org.jspecify.annotations.NonNull;

public class CentrifugeScreen extends BaseScreen<CentrifugeMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("centrifuge/background");

    private final CentrifugeBlockEntity tileEntity;
    private TankWidget tankWidget;

    public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
        tileEntity = menu.getEntity();
        titleLabelX = 7;
        inventoryLabelX = 7;
        inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();
        tankWidget = TankWidget.selectable(leftPos + 148, topPos + 27, 12, 54, tileEntity::tankData);
        addRenderableWidget(tankWidget);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, i, j, 180, 200);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (tankWidget != null && tankWidget.isMouseOver(x, y) && tankWidget.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        // RID: Blit-ing an empty tank sprite on top of the rendered fluid to keep the shine in the foreground.
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 180, 0, leftPos + 147, topPos + 26, 12, 54);
    }
}