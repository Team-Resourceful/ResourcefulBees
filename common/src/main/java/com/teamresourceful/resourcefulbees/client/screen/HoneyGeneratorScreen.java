package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyGeneratorMenu;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

public class HoneyGeneratorScreen extends AbstractContainerScreen<HoneyGeneratorMenu> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/generator/honey_gen.png");

    public HoneyGeneratorScreen(HoneyGeneratorMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
            EnergyContainer container = this.menu.getEntity().getEnergyStorage();
            renderEnergy(graphics, i + 136, j + 16, (float) (container.getStoredEnergy() / ((double) container.getMaxCapacity())));

            FluidHolder holder = menu.getEntity().getFluid();

            int height = (int) (54 * ((float) holder.getFluidAmount() / menu.getEntity().getFluidContainer().getTankCapacity(0)));
            ClientRenderUtils.drawFluid(graphics, height, 12, holder, i + 28, j + 16 + (54 - height));

            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(i, j, 0);
                pose.scale(.8f, .8f, .8f);
                graphics.drawString(this.font, "Fluid: ", 57, 22, 0xffffff);
                graphics.drawString(this.font, holder.isEmpty() ? GuiTranslations.NO_FLUID : ClientFluidHooks.getDisplayName(holder), 61, 32, 0xffffff);
                graphics.drawString(this.font, "Amount: ", 57, 42, 0xffffff);
                graphics.drawString(this.font, FluidConstants.toMillibuckets(holder.getFluidAmount()) + "mB", 61, 52, 0xffffff);
            }
        }
    }

    private void renderEnergy(GuiGraphics graphics, int x, int y, float percentage) {
        int scaledRF = (int) (54 * percentage);
        graphics.blit(BACKGROUND, x, y + (54 - scaledRF), 176, 54 - scaledRF, 12, scaledRF);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        renderFluidTooltip(mouseX, mouseY);
        renderEnergyTooltip(mouseX, mouseY);
    }

    public void renderEnergyTooltip(int mouseX, int mouseY) {
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 136, this.leftPos + 148) && MathUtils.inRangeInclusive(mouseY, this.topPos + 16, this.topPos + 70)) {
            if (Screen.hasShiftDown() || this.menu.getEntity().getEnergyStorage().getStoredEnergy() < 500) {
                setTooltipForNextRenderPass(Component.literal(NumberFormat.getInstance().format(this.menu.getEntity().getEnergyStorage().getStoredEnergy()) + " RF"));
            } else {
                setTooltipForNextRenderPass(Component.literal(TextUtils.NUMBER_FORMAT.format((double) this.menu.getEntity().getEnergyStorage().getStoredEnergy() / 1000) + " kRF"));
            }
        }
    }

    public void renderFluidTooltip(int mouseX, int mouseY) {
        if (MathUtils.inRangeInclusive(mouseX, this.leftPos + 28, this.leftPos + 40) && MathUtils.inRangeInclusive(mouseY, this.topPos + 16, this.topPos + 70)) {
            FluidHolder holder = this.menu.getEntity().getFluid();
            if (Screen.hasShiftDown() || holder.getFluidAmount() < 500) {
                setTooltipForNextRenderPass(Component.literal(FluidConstants.toMillibuckets(holder.getFluidAmount()) + " MB"));
            } else {
                setTooltipForNextRenderPass(Component.literal(TextUtils.NUMBER_FORMAT.format((double) holder.getFluidAmount() / FluidConstants.getBucketAmount()) + " Buckets"));
            }
        }
    }
}
