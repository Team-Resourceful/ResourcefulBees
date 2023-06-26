package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemSlotWidget implements Renderable {

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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(TEXTURE, this.x, this.y, 0, 0, 20, 20, 20, 60);
        graphics.renderItem(this.stack, this.x + 2, this.y + 2);

        if (mouseX >= this.x && mouseX <= this.x + 20 && mouseY >= this.y && mouseY <= this.y + 20 && this.tooltip) {
            ScreenUtils.setTooltip(stack.getHoverName());
        }
    }

}
