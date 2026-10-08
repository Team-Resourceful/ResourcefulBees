package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.honey;

import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyFoodData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class HoneyFoodWidget extends AbstractWidget {

    private static final Identifier SATURATION = ModIdentifier.of("beepedia/saturation");
    private static final Identifier HUNGER = ModIdentifier.of("beepedia/hunger");
    private static final Identifier HUNGER_BAR = ModIdentifier.of("beepedia/hunger_bar");
    private static final int WIDTH = 90;
    private static final int HEIGHT = 9;

    private final HoneyFoodData food;

    public HoneyFoodWidget(HoneyFoodData food) {
        super(0, 0, WIDTH, HEIGHT, Component.empty());
        this.food = food;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int nutrition = Math.min(food.nutrition(), 20);

        float saturation = Math.min(nutrition * food.saturation() * 2.0f, 20.0f);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HUNGER_BAR, getX(), getY(), 0, 0, 90, 9, 90, 9);

        float percent = saturation / 20.0f;
        int saturationWidth = (int) (percent * 90);

        if (saturationWidth > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SATURATION, 90, 9, 90 - saturationWidth, 0, getX() + 90 - saturationWidth, getY(), saturationWidth, 9);
        }

        int full = nutrition / 2;
        int startX = (10 - full) * 9;

        if (nutrition % 2 == 1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HUNGER, 9, 18, 0, 9, getX() + startX - 9, getY(), 9, 9);
        }

        for (int i = 0; i < full; i++) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HUNGER, 9, 18, 0, 0, getX() + startX, getY(), 9, 9);

            startX += 9;
        }
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.literal("Nutrition " + food.nutrition()));
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}