package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.BatteryWidget;
import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyGeneratorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class HoneyGeneratorScreen extends BaseScreen<HoneyGeneratorMenu> {

    public static final Identifier BACKGROUND = ModIdentifier.of("generator/background");

    public HoneyGeneratorScreen(HoneyGeneratorMenu screenContainer, Inventory inventory, Component titleIn) {
        super(screenContainer, inventory, titleIn);
        titleLabelX = 7;
        inventoryLabelX = 7;
        inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();
        HoneyGeneratorBlockEntity honeyGen = menu.getEntity();
        addRenderableWidget(TankWidget.single(leftPos + 30, topPos + 27, 12, 54, honeyGen::tankData));
        addRenderableWidget(new BatteryWidget(leftPos + 138, topPos + 27, 12, 54, 180, 0, BACKGROUND, honeyGen::batteryData));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, leftPos, topPos, 180, 200);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        FluidStack fluid = menu.getEntity().tankData().fluid();

        graphics.text(font, Component.literal("Fluid:"), 47, 30, 0xFF4f312b, false);
        graphics.text(font, Component.literal("Fluid:"), 47, 29, 0xFFf1d65d, false);

        graphics.drawScrollingString(graphics.textRenderer(), font, fluid.isEmpty()
                ? GuiTranslations.NO_FLUID.copy().withColor(0xFF4f312b).withoutShadow()
                : fluid.getHoverName().copy().withColor(0xFF4f312b).withoutShadow()
                , 47, 133, 41);
        graphics.drawScrollingString(graphics.textRenderer(), font, fluid.isEmpty()
                        ? GuiTranslations.NO_FLUID.copy().withColor(0xFFf1d65d).withoutShadow()
                        : fluid.getHoverName().copy().withColor(0xFFf1d65d).withoutShadow()
                , 47, 133, 40);

        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 47, 52, 0xFF4f312b, false);
        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 47, 51, 0xFFf1d65d, false);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        // RID: Blit-ing an empty tank sprite on top of the rendered fluid to keep the shine in the foreground.
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 192, 0, leftPos + 29, topPos + 26, 12, 54);

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
