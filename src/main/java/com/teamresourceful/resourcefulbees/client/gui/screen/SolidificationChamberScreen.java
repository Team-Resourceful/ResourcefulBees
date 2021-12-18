package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.blockentity.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class SolidificationChamberScreen extends AbstractContainerScreen<SolidificationChamberMenu> {

    private final SolidificationChamberBlockEntity tileEntity;

    public SolidificationChamberScreen(SolidificationChamberMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getEntity();
        titleLabelY -= 3;
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/solidification/solidification.png");
        if (tileEntity != null) {
            RenderUtils.bindTexture(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            FluidStack fluidStack = tileEntity.getTank().getFluid();
            int height = (int) ((fluidStack.getAmount() / 16000f) * 62);
            RenderUtils.drawFluid(matrix, height, 14, fluidStack, i + 67, j + 12+(62-height), getBlitOffset());
        }
    }

    private void renderProgressBar(PoseStack matrix) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/solidification/solidification.png");
        RenderUtils.bindTexture(texture);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrix, i + 84, j + 17, 176, 0, 24, (int) (34 * tileEntity.getProcessPercent()));
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderProgressBar(matrix);
            this.renderTooltip(matrix, mouseX, mouseY);
            if (mouseX >= this.leftPos + 67 && mouseX <= this.leftPos + 81 && mouseY >= this.topPos + 12 && mouseY <= this.topPos + 74) {
                if (Screen.hasShiftDown() || tileEntity.getTank().getFluidAmount() < 1000) {
                    this.renderTooltip(matrix, new TextComponent(tileEntity.getTank().getFluidAmount() + " MB"), mouseX, mouseY);
                } else {
                    this.renderTooltip(matrix, new TextComponent(ModConstants.DECIMAL_FORMAT.format((double) tileEntity.getTank().getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
                }
            }
        }
    }

}
