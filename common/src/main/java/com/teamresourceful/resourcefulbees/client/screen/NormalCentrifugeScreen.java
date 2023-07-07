package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.SelectableFluidWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.menus.CentrifugeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class NormalCentrifugeScreen extends AbstractContainerScreen<CentrifugeMenu> {

    private final CentrifugeBlockEntity tileEntity;

    public NormalCentrifugeScreen(CentrifugeMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
        addRenderableWidget(new SelectableFluidWidget(tileEntity.getSelectableFluidContainer(), tileEntity.getBlkPos(), this.leftPos + 152, this.topPos + 11, 16, 64, CentrifugeTranslations.TANK));
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuge/basic.png");
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(graphics);
            super.render(graphics, mouseX, mouseY, partialTicks);
            this.renderTooltip(graphics, mouseX, mouseY);
        }
    }

}