package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.screen.base.CustomContainerScreen;
import com.teamresourceful.resourcefulbees.client.screen.base.CustomWidgets;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class HoneyGeneratorScreen extends CustomContainerScreen<HoneyGeneratorMenu, HoneyGeneratorBlockEntity> {

    public HoneyGeneratorScreen(HoneyGeneratorMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn, 180, 200);

        this.background = ModIdentifier.of("textures/gui/containers/generator.png");
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();

        this.putAt(CustomWidgets.tank(this.block()::tankData).withSize(14, 56), 29, 26);
        this.putAt(CustomWidgets.battery(this.block()::batteryData).withSize(12, 54), 138, 27);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        FluidStack fluid = menu.getEntity().tankData().fluid();

        graphics.text(font, Component.literal("Fluid:"), 47, 31, 0xFF4f312b, false);
        graphics.text(font, Component.literal("Fluid:"), 47, 30, 0xFFf1d65d, false);

        graphics.drawScrollingString(graphics.textRenderer(), font, fluid.isEmpty()
                ? GuiTranslations.NO_FLUID.copy().withColor(0xFF4f312b).withoutShadow()
                : fluid.getHoverName().copy().withColor(0xFF4f312b).withoutShadow()
                , 47, 133, 41);
        graphics.drawScrollingString(graphics.textRenderer(), font, fluid.isEmpty()
                        ? GuiTranslations.NO_FLUID.copy().withColor(0xFFf1d65d).withoutShadow()
                        : fluid.getHoverName().copy().withColor(0xFFf1d65d).withoutShadow()
                , 47, 133, 40);

        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 47, 51, 0xFF4f312b, false);
        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 47, 50, 0xFFf1d65d, false);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        Slot slot = getHoveredSlot();
        if (slot != null && !slot.hasItem()) {
            switch (slot.index) {
                case HoneyGeneratorBlockEntity.TANK_CAP_UPGRADE_SLOT ->
                        graphics.setTooltipForNextFrame(
                                Component.literal("Tank Capacity Upgrade"),
                                mouseX,
                                mouseY
                        );

                case HoneyGeneratorBlockEntity.ENERGY_CAP_UPGRADE_SLOT ->
                        graphics.setTooltipForNextFrame(
                                Component.literal("Energy Capacity Upgrade"),
                                mouseX,
                                mouseY
                        );

                case HoneyGeneratorBlockEntity.ENERGY_XFER_UPGRADE_SLOT ->
                        graphics.setTooltipForNextFrame(
                                Component.literal("Energy Transfer Upgrade"),
                                mouseX,
                                mouseY
                        );

                case HoneyGeneratorBlockEntity.ENERGY_FILL_UPGRADE_SLOT ->
                        graphics.setTooltipForNextFrame(
                                Component.literal("Energy Generation Upgrade"),
                                mouseX,
                                mouseY
                        );
            }
        }
    }
}
