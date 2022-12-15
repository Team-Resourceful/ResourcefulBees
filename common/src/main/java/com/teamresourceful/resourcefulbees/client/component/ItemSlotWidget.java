package com.teamresourceful.resourcefulbees.client.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemSlotWidget implements Widget, TooltipProvider {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/list_button.png");

    private final int x;
    private final int y;
    private final ItemStack stack;
    private final boolean tooltip;

    public ItemSlotWidget(int x, int y, ItemStack stack) {
        this(x, y, stack, true);
    }

    public ItemSlotWidget(int x, int y, ItemStack stack, boolean tooltip) {
        this.x = x;
        this.y = y;
        this.stack = stack;
        this.tooltip = tooltip;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(TEXTURE);
        GuiComponent.blit(stack, this.x, this.y, 0, 0, 20, 20, 20, 60);
        RenderUtils.renderItem(stack, this.stack, this.x + 2, this.y + 2);
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        if (mouseX >= this.x && mouseX <= this.x + 20 && mouseY >= this.y && mouseY <= this.y + 20 && this.tooltip) {
            return List.of(stack.getHoverName());
        }
        return List.of();
    }
}
