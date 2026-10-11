package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class SolidificationChamberScreen extends BaseScreen<SolidificationChamberMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("solidification/background");

    private final SolidificationChamberBlockEntity tileEntity;

    public SolidificationChamberScreen(SolidificationChamberMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
        tileEntity = menu.getEntity();
        titleLabelX = 7;
        inventoryLabelX = 7;
        inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(TankWidget.single(leftPos + 64, topPos + 27, 12, 54, tileEntity::tankData));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        // RID: Added as the texture background wasn't rendering properly
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, leftPos, topPos, 180, 200);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        // RID: Blit-ing an empty tank sprite on top of the rendered fluid to keep the shine in the foreground.
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 180, 0, leftPos + 63, topPos + 26, 12, 54);
    }
}
