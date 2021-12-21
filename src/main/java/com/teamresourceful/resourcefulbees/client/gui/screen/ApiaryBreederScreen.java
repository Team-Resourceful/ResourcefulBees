package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBreederBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.menus.ApiaryBreederContainer;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ApiaryBreederScreen extends AbstractContainerScreen<ApiaryBreederContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_breeder_gui.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    private final ApiaryBreederBlockEntity apiaryBreederBlockEntity;

    public ApiaryBreederScreen(ApiaryBreederContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        apiaryBreederBlockEntity = this.menu.getEntity();
        preInit();
    }

    protected void preInit(){
        this.imageWidth = 198;
        this.imageHeight = 148 + this.menu.getNumberOfBreeders() * 20;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (getApiaryBreederTileEntity() != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (this.menu.isRebuild()) {
            preInit();
            init();
            this.menu.setRebuild(false);
        }
        RenderUtils.bindTexture(BACKGROUND);
        blit(matrix, this.leftPos, this.topPos, 0, 0, 198, 188);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        for (Widget widget : this.renderables) {
            if (widget instanceof AbstractWidget aWidget && aWidget.isMouseOver(mouseX, mouseY)) {
                aWidget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }

    public ApiaryBreederBlockEntity getApiaryBreederTileEntity() {
        return apiaryBreederBlockEntity;
    }
}
