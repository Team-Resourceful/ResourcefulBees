package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.screen.base.CustomContainerScreen;
import com.teamresourceful.resourcefulbees.client.screen.base.CustomWidgets;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyPotBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyPotMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class HoneyPotScreen extends CustomContainerScreen<HoneyPotMenu, HoneyPotBlockEntity> {

    public HoneyPotScreen(HoneyPotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 180, 200);

        this.background = ModIdentifier.of("textures/gui/containers/honey_pot.png");
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();

        this.putAt(CustomWidgets.tank(this.block()::tankData).withSize(14, 56), 25, 26);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        FluidStack fluid = menu.getEntity().tankData().fluid();

        graphics.text(font, Component.literal("Fluid:"), 51, 29, 0xFF4f312b, false);
        graphics.text(font, Component.literal("Fluid:"), 51, 28, 0xFFf1d65d, false);

        graphics.drawScrollingString(graphics.textRenderer(), font, fluid.isEmpty()
                        ? GuiTranslations.NO_FLUID.copy().withColor(0xFF4f312b).withoutShadow()
                        : fluid.getHoverName().copy().withColor(0xFF4f312b).withoutShadow()
                , 51, 142, 41);
        graphics.drawScrollingString(graphics.textRenderer(), font, fluid.isEmpty()
                        ? GuiTranslations.NO_FLUID.copy().withColor(0xFFf1d65d).withoutShadow()
                        : fluid.getHoverName().copy().withColor(0xFFf1d65d).withoutShadow()
                , 51, 142, 40);

        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 51, 74, 0xFF4f312b, false);
        graphics.text(font, Component.literal("Amount: " + fluid.amount() + " mB"), 51, 73, 0xFFf1d65d, false);
    }
}