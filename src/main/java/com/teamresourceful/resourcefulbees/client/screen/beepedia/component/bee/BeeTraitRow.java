package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class BeeTraitRow extends AbstractWidget {

    private static final int HEIGHT = 28;

    private final Trait trait;
    private final ItemStack icon;
    private final Runnable callback;

    public BeeTraitRow(Trait trait, int width, Runnable callback) {
        super(0, 0, width, HEIGHT, trait.getDisplayName());

        this.trait = trait;
        this.icon = new ItemStack(trait.displayItem());
        this.callback = callback;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        var font = Minecraft.getInstance().font;

        int iconX = getX() + 4;
        int iconY = getY() + 6;
        int textX = getX() + 24;

        graphics.item(icon, iconX, iconY);
        graphics.text(font, trait.getDisplayName(), textX, getY() + 4, 0xFFFFFFFF);
        graphics.text(font, Component.literal(trait.name()), textX, getY() + 15, 0xFF777777);

        if (BeepediaScreenUtil.isInside(mouseX, mouseY, iconX, iconY, 16, 16)) {
            graphics.setTooltipForNextFrame(font, icon, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y())) {
            return false;
        }

        callback.run();
        return true;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, trait.getDisplayName());
    }

}