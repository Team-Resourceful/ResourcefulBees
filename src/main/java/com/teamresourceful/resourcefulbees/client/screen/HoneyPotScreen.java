package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyPotMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class HoneyPotScreen extends AbstractContainerScreen<HoneyPotMenu> {

    private static final Identifier BACKGROUND =
            ModIdentifier.of("textures/gui/honey_tank/honey_pot.png");

    public HoneyPotScreen(HoneyPotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(TankWidget.single(leftPos + 129, topPos + 16, 12, 54, () -> menu.getEntity().tankData()));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        FluidStack fluid = menu.getEntity().tankData().fluid();
        graphics.text(font, Component.literal("Fluid:"), 36, 17, 0xFFFFFFFF, false);

        graphics.drawScrollingString(
                graphics.textRenderer(),
                font,
                fluid.isEmpty()
                        ? GuiTranslations.NO_FLUID
                        : fluid.getHoverName(),
                40,
                118,
                27
        );

        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 36, 39, 0xFFFFFFFF, false);
    }
}