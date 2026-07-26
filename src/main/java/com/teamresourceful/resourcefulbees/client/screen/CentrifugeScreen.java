package com.teamresourceful.resourcefulbees.client.screen;

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

    public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
        tileEntity = menu.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
        //addRenderableWidget(new SelectableFluidWidget(tileEntity.getSelectableFluidContainer(), tileEntity.getBlkPos(), this.leftPos + 152, this.topPos + 11, 16, 64, CentrifugeTranslations.TANK));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);
        }
    }

    /*    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/centrifuge/basic.png");
        if (tileEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            super.render(graphics, mouseX, mouseY, partialTicks);
            this.renderTooltip(graphics, mouseX, mouseY);
        }
    }*/

}