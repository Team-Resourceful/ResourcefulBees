package com.teamresourceful.resourcefulbees.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.NonNull;

public class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	public BaseScreen(T menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX + 2, titleLabelY + 1, 0xFFf1d65d, false);
		graphics.text(font, title, titleLabelX + 2, titleLabelY, 0xFF683e36, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX + 2, inventoryLabelY + 1, 0xFFc18332, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX + 2, inventoryLabelY, 0xFF683e36, false);
	}

	// RID: Slot Hover Effect - Credit: AE2.
	@Override
	public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);

		Slot slot = getHoveredSlot();
		if (slot != null) {
			int x = leftPos + slot.x;
			int y = topPos + slot.y;
			int w = 16;
			int h = 16;
			graphics.horizontalLine(x, x + w, y - 1, 0xFFf3f59d);
			graphics.horizontalLine(x - 1, x + w, y + h, 0xFFf3f59d);
			graphics.verticalLine(x - 1, y - 2, y + h, 0xFFf3f59d);
			graphics.verticalLine(x + w, y - 2, y + h, 0xFFf3f59d);
			graphics.fillGradient(x, y, x + w, y + h, 0x66eeb044, 0x66eeb044);
		}
	}
}