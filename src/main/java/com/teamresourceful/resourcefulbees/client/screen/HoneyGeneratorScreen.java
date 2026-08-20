package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.BatteryWidget;
import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class HoneyGeneratorScreen extends AbstractContainerScreen<HoneyGeneratorMenu> {

    public static final Identifier BACKGROUND = ModIdentifier.of("textures/gui/generator/honey_gen.png");

    public HoneyGeneratorScreen(HoneyGeneratorMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        HoneyGeneratorBlockEntity honeyGen = menu.getEntity();
        addRenderableWidget(new TankWidget(leftPos + 28, topPos + 16, 12, 54, honeyGen::tankData));
        addRenderableWidget(new BatteryWidget(leftPos + 136, topPos + 16, 12, 54, 176, 0, BACKGROUND, honeyGen::batteryData));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int x = leftPos;
        int y = topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
        super.extractLabels(graphics, mouseX, mouseY);

        FluidStack fluid = menu.getEntity().tankData().getFirst().fluid();

        graphics.text(
                font,
                Component.literal("Fluid:"),
                45,
                18,
                0xFFFFFFFF,
                false
        );

        graphics.drawScrollingString(
                graphics.textRenderer(),
                font,
                fluid.isEmpty()
                        ? GuiTranslations.NO_FLUID
                        : fluid.getHoverName(),
                45,
                130,
                28
        );

        graphics.text(
                font,
                Component.literal("Amount: " + fluid.amount() + " mB"),
                45,
                39,
                0xFFFFFFFF,
                false
        );
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
