package com.teamresourceful.resourcefulbees.client.screen.base;

import com.teamresourceful.resourcefulbees.common.menus.base.AbstractModContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;

public class CustomContainerScreen<M extends AbstractModContainerMenu<B>, B extends BlockEntity> extends AbstractContainerScreen<M> {

	protected Identifier background;

	public CustomContainerScreen(M menu, Inventory inventory, Component title, int width, int height) {
		super(menu, inventory, title, width, height);
	}

	protected B block() {
		return this.menu.getEntity();
	}

	protected <T extends AbstractWidget> T putAt(T widget, int x, int y) {
		this.addRenderableWidget(widget);
		widget.setPosition(x + this.leftPos, y + this.topPos);
		return widget;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		var scrolled = super.mouseScrolled(x, y, scrollX, scrollY);
		return scrolled || this.getChildAt(x, y).filter((child) -> child.mouseScrolled(x, y, scrollX, scrollY)).isPresent();
	}

	@Override
	protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX + 2, titleLabelY + 1, 0xFFf1d65d, false);
		graphics.text(font, title, titleLabelX + 2, titleLabelY, 0xFF683e36, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX + 2, inventoryLabelY + 1, 0xFFc18332, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX + 2, inventoryLabelY, 0xFF683e36, false);
	}

	protected void extractSurface(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(graphics, mouseX, mouseY, partialTicks);

		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				this.background,
				this.leftPos, this.topPos,
				0, 0,
				this.imageWidth, this.imageHeight,
				this.imageWidth, this.imageHeight
		);

		graphics.pose().pushMatrix();
		graphics.pose().translate(this.leftPos, this.topPos);
		this.extractSurface(graphics, mouseX, mouseY, partialTicks);
		graphics.pose().popMatrix();
	}

	// RID: Slot Hover Effect - Credit: AE2.
	@Override
	public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractContents(graphics, mouseX, mouseY, partialTicks);

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