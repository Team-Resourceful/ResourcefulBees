package com.teamresourceful.resourcefulbees.client.screen.locator;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class BeeLocatorEntry extends ObjectSelectionList.Entry<BeeLocatorEntry> {

    private static final int PREVIEW_SIZE = 30;

    private final Consumer<BeeLocatorEntry> selector;
    private final Entity displayEntity;
    private final Component displayName;

    public BeeLocatorEntry(Consumer<BeeLocatorEntry> selector, @NotNull Entity displayEntity, @NotNull Component displayName) {
        this.selector = selector;
        this.displayEntity = displayEntity;
        this.displayName = displayName;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public Identifier getType() {
        if (displayEntity.getType() instanceof CustomBeeEntityType<?> beeType) {
            return beeType.getBeeType();
        }

        return null;
    }

    @Override
    public @NotNull Component getNarration() {
        return getDisplayName();
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        int x = getContentX();
        int y = getContentY();

        ClientRenderUtils.renderEntity(graphics, displayEntity, x + 2, y + 2, PREVIEW_SIZE, PREVIEW_SIZE, -135.0F, 0.5F);

        graphics.text(minecraft.font, displayName, x + PREVIEW_SIZE + 8, y + 11, 0xFFA0A0A0);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        selector.accept(this);
        return true;
    }
}