package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.HoneyPotMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class HoneyPotScreen extends BaseScreen<HoneyPotMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("honey_pot/background");

    public HoneyPotScreen(HoneyPotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        titleLabelX = 7;
        inventoryLabelX = 7;
        inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(TankWidget.single(leftPos + 26, topPos + 27, 12, 54, () -> menu.getEntity().tankData()));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, leftPos, topPos, 180, 200);
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

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        // RID: Blit-ing an empty tank sprite on top of the rendered fluid to keep the shine in the foreground.
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 180, 0, leftPos + 25, topPos + 26, 12, 54);
    }
}