package com.teamresourceful.resourcefulbees.client.gui.screen.locator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntityType;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BeeLocatorEntry extends ObjectSelectionList.Entry<BeeLocatorEntry> {

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

    public String getType() {
        return displayEntity.getType() instanceof CustomBeeEntityType<?> customBeeEntityType ? customBeeEntityType.getBeeType() : null;
    }

    @Override
    public @NotNull Component getNarration() {
        return displayName;
    }

    @Override
    public void render(@NotNull PoseStack stack, int id, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        Minecraft instance = Minecraft.getInstance();
        Font font = instance.font;
        GuiComponent.drawString(stack, font, displayName, left + 30, top + 5, 10526880);
        try (var pose = new CloseablePoseStack(stack)) {
            RenderUtils.renderEntity(stack, this.displayEntity, left + 5, top + 5, 45F, 1f);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.selector.accept(this);
        return false;
    }
}
