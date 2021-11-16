package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyCongealerContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyCongealerTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class HoneyCongealerScreen extends ContainerScreen<HoneyCongealerContainer> {

    private final HoneyCongealerTileEntity tileEntity;

    public HoneyCongealerScreen(HoneyCongealerContainer container, PlayerInventory inventory, ITextComponent displayName) {
        super(container, inventory, displayName);
        tileEntity = container.getHoneyCongealerTileEntity();
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/congealer/honey_congealer.png");
        Minecraft client = this.minecraft;
        if (client != null && tileEntity != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            FluidStack fluidStack = tileEntity.getTank().getFluid();
            int height = (int) ((fluidStack.getAmount() / 16000f) * 62);
            RenderUtils.drawFluid(matrix, height, 14, fluidStack, i + 67, j + 12+(62-height), getBlitOffset());
        }
    }

    private void renderProgressBar(MatrixStack matrix) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/congealer/honey_congealer.png");
        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(texture);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i + 84, j + 17, 176, 0, 24, (int) (34 * tileEntity.getProcessPercent()));
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.tileEntity != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderProgressBar(matrix);
            this.renderTooltip(matrix, mouseX, mouseY);
            if (mouseX >= this.leftPos + 67 && mouseX <= this.leftPos + 81 && mouseY >= this.topPos + 12 && mouseY <= this.topPos + 74) {
                if (Screen.hasShiftDown() || tileEntity.getTank().getFluidAmount() < 500) {
                    this.renderTooltip(matrix, new StringTextComponent(tileEntity.getTank().getFluidAmount() + " MB"), mouseX, mouseY);
                } else {
                    this.renderTooltip(matrix, new StringTextComponent(ModConstants.DECIMAL_FORMAT.format((double) tileEntity.getTank().getFluidAmount() / 1000) + " Buckets"), mouseX, mouseY);
                }
            }
        }
    }

}
