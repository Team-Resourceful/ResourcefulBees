package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.components.ImageButton;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SlotButton extends ImageButton {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/list_button.png");

    private final BooleanSupplier isActive;
    private final ResourceLocation texture;
    private final Runnable onPress;

    private Supplier<List<Component>> tooltip;

    public SlotButton(int x, int y, ResourceLocation texture, BooleanSupplier isActive, Runnable onPress) {
        super(x, y, 20, 20);
        this.imageHeight = 60;
        this.imageWidth = 20;
        this.isActive = isActive;
        this.texture = texture;
        this.onPress = onPress;
    }

    public void setTooltipProvider(Supplier<List<Component>> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(this.texture, this.getX() + 2, this.getY() + 2, 0, 0, 16, 16, 16, 16);

        if (this.tooltip != null && this.isHovered) {
            ScreenUtils.setTooltip(this.tooltip.get());
        }
    }

    @Override
    public ResourceLocation getTexture(int mouseX, int mouseY) {
        return TEXTURE;
    }

    @Override
    public int getU(int mouseX, int mouseY) {
        return 0;
    }

    @Override
    public int getV(int mouseX, int mouseY) {
        return isActive.getAsBoolean() ? 40 : isHovered ? 20 : 0;
    }

    @Override
    public void onPress() {
        onPress.run();
    }
}
