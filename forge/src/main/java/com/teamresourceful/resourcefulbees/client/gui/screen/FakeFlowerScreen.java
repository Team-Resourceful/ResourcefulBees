package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.components.BeeconEffectWidget;
import com.teamresourceful.resourcefulbees.common.blockentity.FakeFlowerEntity;
import com.teamresourceful.resourcefulbees.common.inventory.menus.FakeFlowerMenu;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FakeFlowerScreen extends AbstractContainerScreen<FakeFlowerMenu> {

    private final FakeFlowerEntity tileEntity;
    public FakeFlowerScreen(FakeFlowerMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getEntity();
        this.imageHeight = 133;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/fake_flower/fake_flower.png");
        if (tileEntity != null) {
            RenderUtils.bindTexture(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }
}
